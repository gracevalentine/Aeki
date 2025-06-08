package com.example.myaeki.Transaction.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Authentication.Model.UserProfileResponse
import com.example.myaeki.R
import com.example.myaeki.Transaction.Model.CartItem
import com.example.myaeki.Transaction.Model.CheckoutRequest
import com.example.myaeki.Transaction.Model.CheckoutResponse
import com.example.myaeki.Transaction.Model.TransactionCartResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartViewModel : ViewModel() {

    // State untuk data cart
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // State untuk metode pengantaran
    private val _deliveryMethod = MutableStateFlow<String?>(null)
    val deliveryMethod: StateFlow<String?> = _deliveryMethod

    // State untuk alamat user
    private val _userAddress = MutableStateFlow<String?>(null)
    private val _userPostalCode = MutableStateFlow<String?>(null)
    val userAddress: StateFlow<String?> = _userAddress
    val userPostalCode: StateFlow<String?> = _userPostalCode

    // Ambil data keranjang berdasarkan userId
    fun fetchCart(userId: Int) {
        ApiClient.transactionService.getCartByUserId(userId)
            .enqueue(object : Callback<TransactionCartResponse> {
                override fun onResponse(
                    call: Call<TransactionCartResponse>,
                    response: Response<TransactionCartResponse>
                ) {
                    if (response.isSuccessful) {
                        _cartItems.value = response.body()?.data.orEmpty()
                    }
                }

                override fun onFailure(call: Call<TransactionCartResponse>, t: Throwable) {
                    // Bisa log error di sini
                }
            })
    }

    // Ambil data alamat berdasarkan userId
    fun fetchUserAddress(userId: Int) {
        ApiClient.userService.getUserProfile(userId.toString()) // Convert Int ke String saat memanggil API
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        _userAddress.value = response.body()?.user?.address
                        _userPostalCode.value = response.body()?.user?.postal_code

                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    // Log error jika perlu
                }
            })
    }

    // Update kuantitas produk
    fun updateQuantity(productId: Int, increment: Boolean) {
        viewModelScope.launch {
            val updated = _cartItems.value.map {
                if (it.product_id == productId) {
                    val newQty = if (increment) it.quantity + 1 else (it.quantity - 1).coerceAtLeast(0)
                    it.copy(quantity = newQty)
                } else it
            }.filter { it.quantity > 0 }
            _cartItems.value = updated
        }
    }

    // Hapus 1 produk dari cart
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _cartItems.value = _cartItems.value.filter { it.product_id != productId }
        }
    }

    // Hapus semua produk dari cart
    fun deleteAllProducts() {
        viewModelScope.launch {
            _cartItems.value = emptyList()
        }
    }

    // Set metode pengantaran
    fun setDeliveryMethod(method: String) {
        _deliveryMethod.value = method
    }

    // Hitung total harga
    fun getTotalPrice(): Double {
        val subtotal = _cartItems.value.sumOf { it.product_price * it.quantity }
        val deliveryFee = if (_deliveryMethod.value == "Delivery") 25000.0 else 0.0
        return subtotal + deliveryFee
    }
}

class TransactionViewModel : ViewModel() {

    fun buyProduct(request: CheckoutRequest): LiveData<CheckoutResponse> {
        val result = MutableLiveData<CheckoutResponse>()

        ApiClient.transactionService.checkout(request).enqueue(object : Callback<CheckoutResponse> {
            override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
                if (response.isSuccessful) {
                    result.value = response.body() ?: CheckoutResponse(success = false, message = "Tidak ada respons")
                } else {
                    result.value = CheckoutResponse(success = false, message = "Gagal: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                result.value = CheckoutResponse(success = false, message = "Error: ${t.message}")
            }
        })

        return result
    }
}

class CartFragment : Fragment() {

    private val viewModel: CartViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels() // Tambahkan ini
    private lateinit var cartAdapter: CartAdapter // Tambahkan ini

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", 0)
        val userIdString = sharedPref.getString("USER_ID", null)

        if (userIdString != null) {
            val userId = userIdString.toIntOrNull()
            if (userId != null) {
                viewModel.fetchCart(userId)
                viewModel.fetchUserAddress(userId)
            } else {
                Toast.makeText(requireContext(), "USER_ID tidak valid", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
        }

        val countProductText: TextView = view.findViewById(R.id.countProduct)
        val countPriceText: TextView = view.findViewById(R.id.countPrice)
        val deleteAllButton: Button = view.findViewById(R.id.button_delete_all)
        val checkoutButton: Button = view.findViewById(R.id.buttonCheckOut)
        val deliveryButton: View = view.findViewById(R.id.buttonDelivery)
        val pickUpButton: View = view.findViewById(R.id.buttonPickUp)
        val productCounter: TextView = view.findViewById(R.id.productCounter)
        val biayaSubTotal: TextView = view.findViewById(R.id.biayaSubTotal)
        val biayaPengantaran: TextView = view.findViewById(R.id.biayaPengantaran)
        val totalBiaya: TextView = view.findViewById(R.id.totalBiaya)
        val alamatText: TextView = view.findViewById(R.id.detailAlamat)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerCart)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.cartItems.collectLatest { items ->
                countProductText.text = "${items.sumOf { it.quantity }} produk"
                countPriceText.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
                productCounter.text = items.sumOf { it.quantity }.toString()
                biayaSubTotal.text =
                    "Rp ${String.format("%,.0f", items.sumOf { it.product_price * it.quantity })}"
                totalBiaya.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"

                recyclerView.adapter = CartAdapter(
                    items,
                    onPlusClick = { viewModel.updateQuantity(it.product_id, true) },
                    onMinusClick = { viewModel.updateQuantity(it.product_id, false) },
                    onDeleteClick = { viewModel.deleteProduct(it.product_id) }
                )
            }
        }

        lifecycleScope.launch {
            viewModel.deliveryMethod.collectLatest { method ->
                biayaPengantaran.text = if (method == "Delivery") "Rp 25.000" else "GRATIS"
                totalBiaya.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
            }
        }

        // ✅ Mengamati alamat user
        lifecycleScope.launch {
            viewModel.userAddress.collectLatest { address ->
                alamatText.text = address ?: "Alamat tidak ditemukan"
            }
        }

        deleteAllButton.setOnClickListener {
            viewModel.deleteAllProducts()
            Toast.makeText(context, "Semua produk dihapus", Toast.LENGTH_SHORT).show()
        }

        deliveryButton.setOnClickListener {
            viewModel.setDeliveryMethod("Delivery")
            Toast.makeText(context, "Metode pengantaran: Delivery", Toast.LENGTH_SHORT).show()
        }

        pickUpButton.setOnClickListener {
            viewModel.setDeliveryMethod("Pick Up")
            Toast.makeText(context, "Metode pengantaran: Pick Up", Toast.LENGTH_SHORT).show()
        }

        checkoutButton.setOnClickListener {
            val cartItems = viewModel.cartItems.value ?: emptyList()

            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Keranjang kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sharedPref.getString("USER_ID", null)?.toIntOrNull() ?: -1
            if (userId == -1) {
                Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var successCount = 0
            var failCount = 0

            for (item in cartItems) {
                val request = CheckoutRequest(
                    user_id = userId,
                    product_id = item.product_id,
                    quantity = item.quantity
                )

                transactionViewModel.buyProduct(request).observe(viewLifecycleOwner) { result ->
                    if (result.message.contains("berhasil", ignoreCase = true)) {
                        successCount++
                    } else {
                        failCount++
                    }

                    // Cek apakah semua item sudah diproses
                    if (successCount + failCount == cartItems.size) {
                        if (failCount == 0) {
                            Toast.makeText(
                                requireContext(),
                                "Checkout berhasil",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.deleteAllProducts() // Kosongkan cart
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Beberapa produk gagal dibeli",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }
}

