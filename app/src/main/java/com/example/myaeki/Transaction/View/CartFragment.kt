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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _deliveryMethod = MutableStateFlow<String?>(null)
    val deliveryMethod: StateFlow<String?> = _deliveryMethod

    fun fetchCart(userId: Int) {
        ApiClient.transactionService.getCartByUserId(userId)
            .enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                    if (response.isSuccessful) {
                        val items = response.body().orEmpty()
                        val products = items.map {
                            Product(
                                product_id = it.product_id,
                                name = it.product_name,
                                description = "", // bisa ganti kalau backend kirim
                                price = it.product_price,
                                stock_quantity = it.quantity,
                                category = null,
                                image_url = null
                            )
                        }
                        _products.value = products
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    // log error atau toast
                }
            })
    }

    fun updateQuantity(productId: Int, increment: Boolean) {
        viewModelScope.launch {
            val updatedProducts = _products.value.map { product ->
                if (product.product_id == productId) {
                    val newQty = if (increment) product.stock_quantity + 1 else (product.stock_quantity - 1).coerceAtLeast(0)
                    product.copy(stock_quantity = newQty)
                } else product
            }.filter { it.stock_quantity > 0 }
            _products.value = updatedProducts
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _products.value = _products.value.filter { it.product_id != productId }
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            _products.value = emptyList()
        }
    }

    fun setDeliveryMethod(method: String) {
        _deliveryMethod.value = method
    }

    fun getTotalPrice(): Double {
        return _products.value.sumOf { it.price * it.stock_quantity } +
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

        val userId = 1 // Sementara hardcoded, bisa ambil dari session nanti
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
            viewModel.products.collectLatest { products ->
                countProductText.text = "${products.sumOf { it.stock_quantity }} produk"
                countPriceText.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
                productCounter.text = products.sumOf { it.stock_quantity }.toString()
                biayaSubTotal.text = "Rp ${String.format("%,.0f", products.sumOf { it.price * it.stock_quantity })}"
                totalBiaya.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"

                recyclerView.adapter = CartAdapter(
                    products,
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
                viewModel.products.value.isEmpty() ->
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
