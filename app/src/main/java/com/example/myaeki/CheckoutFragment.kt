package com.example.myaeki

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
import com.example.myaeki.Product.Model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

// Data class for product
//data class Product(
//    val id: Int,
//    val name: String,
//    val description: String,
//    val price: Double,
//    var quantity: Int
//)

// ViewModel for managing checkout data
class CheckoutViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(
        listOf(
            Product(1, "OFTAST", "piring, putih, 25 cm", 9900.0, 1, category = null, imageUrl = null),
            Product(2, "SAMLA", "kotak penyimpanan bening", 19900.0, 1, category = null, imageUrl = null),
            Product(3, "REKO", "gelas, putih, kecil", 9900.0, 1, category = null, imageUrl = null)
        )
    )
    val products: StateFlow<List<Product>> = _products

    private val _deliveryMethod = MutableStateFlow<String?>(null)
    val deliveryMethod: StateFlow<String?> = _deliveryMethod

    fun updateQuantity(productId: Int, increment: Boolean) {
        viewModelScope.launch {
            val updatedProducts = _products.value.map { product ->
                if (product.id == productId) {
                    val newQuantity = if (increment) product.quantity + 1 else (product.quantity - 1).coerceAtLeast(0)
                    product.copy(quantity = newQuantity)
                } else {
                    product
                }
            }.filter { it.quantity > 0 }
            _products.value = updatedProducts
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _products.value = _products.value.filter { it.id != productId }
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
        return _products.value.sumOf { it.price * it.quantity } + if (_deliveryMethod.value == "Delivery") 25000.0 else 0.0
    }

    fun getProductCount(): Int {
        return _products.value.sumOf { it.quantity }
    }
}

// Holder untuk setiap produk di UI
data class ProductViewHolder(
    val deleteButton: ImageButton,
    val minusButton: ImageButton,
    val plusButton: ImageButton,
    val quantityText: TextView,
    val productId: Int
)

class CheckoutFragment : Fragment() {

    private val viewModel: CheckoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countProductText: TextView = view.findViewById(R.id.countProduct)
        val countPriceText: TextView = view.findViewById(R.id.countPrice)
        val deleteAllButton: Button = view.findViewById(R.id.button_delete_all)
        val checkoutButton: Button = view.findViewById(R.id.buttonCheckOut)
        val deliveryButton: View = view.findViewById(R.id.buttonDelivery)
        val pickUpButton: View = view.findViewById(R.id.buttonPickUp)

        val productViews = listOf(
            ProductViewHolder(
                view.findViewById(R.id.buttonDelete),
                view.findViewById(R.id.buttonMinus),
                view.findViewById(R.id.buttonPlus),
                view.findViewById(R.id.textQuantity),
                1
            ),
            ProductViewHolder(
                view.findViewById(R.id.buttonDelete2),
                view.findViewById(R.id.buttonMinus2),
                view.findViewById(R.id.buttonPlus2),
                view.findViewById(R.id.textQuantity2),
                2
            ),
            ProductViewHolder(
                view.findViewById(R.id.buttonDelete3),
                view.findViewById(R.id.buttonMinus3),
                view.findViewById(R.id.buttonPlus3),
                view.findViewById(R.id.textQuantity3),
                3
            )
        )

        // Observe product list
        lifecycleScope.launch {
            viewModel.products.collectLatest { products ->
                countProductText.text = "${products.sumOf { it.quantity }} produk"
                countPriceText.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"

                productViews.forEach { holder ->
                    val product = products.find { it.id == holder.productId }
                    holder.quantityText.text = product?.quantity?.toString() ?: "0"
                    holder.deleteButton.setOnClickListener { viewModel.deleteProduct(holder.productId) }
                    holder.minusButton.setOnClickListener { viewModel.updateQuantity(holder.productId, false) }
                    holder.plusButton.setOnClickListener { viewModel.updateQuantity(holder.productId, true) }
                }

                view.findViewById<TextView>(R.id.productCounter).text = products.sumOf { it.quantity }.toString()
                view.findViewById<TextView>(R.id.biayaSubTotal).text = "Rp ${String.format("%,.0f", products.sumOf { it.price * it.quantity })}"
                view.findViewById<TextView>(R.id.totalBiaya).text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
            }
        }

        // Observe delivery method
        lifecycleScope.launch {
            viewModel.deliveryMethod.collectLatest { method ->
                view.findViewById<TextView>(R.id.biayaPengantaran).text = if (method == "Delivery") "Rp 25.000" else "GRATIS"
                view.findViewById<TextView>(R.id.totalBiaya).text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
            }
        }

        // Button handlers
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
                else -> Toast.makeText(context, "Checkout berhasil! Total: Rp ${String.format("%,.0f", viewModel.getTotalPrice())}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
