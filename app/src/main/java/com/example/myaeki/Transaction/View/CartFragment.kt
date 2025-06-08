package com.example.myaeki.Transaction.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Product.Model.Product
import com.example.myaeki.R
import com.example.myaeki.Transaction.Model.CartItem
import com.example.myaeki.Transaction.Model.TransactionCartResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _deliveryMethod = MutableStateFlow<String?>(null)
    val deliveryMethod: StateFlow<String?> = _deliveryMethod

    fun fetchCart(userId: Int) {
        ApiClient.transactionService.getCartByUserId(userId)
            .enqueue(object : Callback<TransactionCartResponse> {
                override fun onResponse(call: Call<TransactionCartResponse>, response: Response<TransactionCartResponse>) {
                    if (response.isSuccessful) {
                        _cartItems.value = response.body()?.data.orEmpty()
                    }
                }

                override fun onFailure(call: Call<TransactionCartResponse>, t: Throwable) {
                    // Log error
                }
            })
    }


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

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _cartItems.value = _cartItems.value.filter { it.product_id != productId }
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            _cartItems.value = emptyList()
        }
    }

    fun setDeliveryMethod(method: String) {
        _deliveryMethod.value = method
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf { it.product_price * it.quantity } +
                if (_deliveryMethod.value == "Delivery") 25000.0 else 0.0
    }
}


class CartFragment : Fragment() {

    private val viewModel: CartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = 10 // Sementara hardcoded, bisa ambil dari session nanti
        viewModel.fetchCart(userId)

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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerCart)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.cartItems.collectLatest { items ->
                countProductText.text = "${items.sumOf { it.quantity }} produk"
                countPriceText.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
                productCounter.text = items.sumOf { it.quantity }.toString()
                biayaSubTotal.text = "Rp ${String.format("%,.0f", items.sumOf { it.product_price * it.quantity })}"
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
            when {
                viewModel.deliveryMethod.value == null ->
                    Toast.makeText(context, "Pilih metode pengantaran terlebih dahulu", Toast.LENGTH_SHORT).show()
                viewModel.cartItems.value.isEmpty() ->
                    Toast.makeText(context, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(
                    context,
                    "Checkout berhasil! Total: Rp ${String.format("%,.0f", viewModel.getTotalPrice())}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
