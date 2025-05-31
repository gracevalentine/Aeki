package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaeki.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class for product
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    var quantity: Int
)

// ViewModel for managing checkout data
class CheckoutViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(
        listOf(
            Product(1, "OFTAST", "piring, putih, 25 cm", 9900.0, 1),
            Product(2, "SAMLA", "kotak penyimpanan bening", 19900.0, 1),
            Product(3, "REKO", "gelas, putih, kecil", 9900.0, 1)
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

class CheckoutFragment : Fragment() {

    private val viewModel: CheckoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        val countProductText: TextView = view.findViewById(R.id.countProduct)
        val countPriceText: TextView = view.findViewById(R.id.countPrice)
        val deleteAllButton: Button = view.findViewById(R.id.button_delete_all)
        val checkoutButton: Button = view.findViewById(R.id.buttonCheckOut)
        val deliveryButton: View = view.findViewById(R.id.buttonDelivery)
        val pickUpButton: View = view.findViewById(R.id.buttonPickUp)

        // Product-specific views
        val productViews = listOf(
            Triple(
                view.findViewById<ImageButton>(R.id.buttonDelete),
                view.findViewById<ImageButton>(R.id.buttonMinus),
                view.findViewById<ImageButton>(R.id.buttonPlus),
                view.findViewById<TextView>(R.id.textQuantity),
                1
            ),
            Triple(
                view.findViewById<ImageButton>(R.id.buttonDelete2),
                view.findViewById<ImageButton>(R.id.buttonMinus2),
                view.findViewById<ImageButton>(R.id.buttonPlus2),
                view.findViewById<TextView>(R.id.textQuantity2),
                2
            ),
            Triple(
                view.findViewById<ImageButton>(R.id.buttonDelete3),
                view.findViewById<ImageButton>(R.id.buttonMinus3),
                view.findViewById<ImageButton>(R.id.buttonPlus3),
                view.findViewById<TextView>(R.id.textQuantity3),
                3
            )
        )

        // Observe ViewModel data
        viewModel.products.observe(viewLifecycleOwner) { products ->
            countProductText.text = "${products.sumOf { it.quantity }} produk"
            countPriceText.text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
            productViews.forEach { (deleteButton, minusButton, plusButton, quantityText, productId) ->
                val product = products.find { it.id == productId }
                quantityText.text = product?.quantity?.toString() ?: "0"
                deleteButton.setOnClickListener { viewModel.deleteProduct(productId) }
                minusButton.setOnClickListener { viewModel.updateQuantity(productId, false) }
                plusButton.setOnClickListener { viewModel.updateQuantity(productId, true) }
            }
            view.findViewById<TextView>(R.id.productCounter).text = products.sumOf { it.quantity }.toString()
            view.findViewById<TextView>(R.id.biayaSubTotal).text = "Rp ${String.format("%,.0f", products.sumOf { it.price * it.quantity })}"
            view.findViewById<TextView>(R.id.totalBiaya).text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
        }

        viewModel.deliveryMethod.observe(viewLifecycleOwner) { method ->
            view.findViewById<TextView>(R.id.biayaPengantaran).text = if (method == "Delivery") "Rp 25.000" else "GRATIS"
            view.findViewById<TextView>(R.id.totalBiaya).text = "Rp ${String.format("%,.0f", viewModel.getTotalPrice())}"
        }

        // Set click listeners
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
            if (viewModel.deliveryMethod.value == null) {
                Toast.makeText(context, "Pilih metode pengantaran terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else if (viewModel.products.value.isEmpty()) {
                Toast.makeText(context, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Checkout berhasil! Total: Rp ${String.format("%,.0f", viewModel.getTotalPrice())}", Toast.LENGTH_LONG).show()
                // Add navigation or further checkout logic here
            }
        }
    }

    private fun Triple(first: ImageButton?, second: ImageButton?, third: ImageButton?, findViewById: TextView?, i: Int): Triple<ImageButton, ImageButton, ImageButton> {

    }
}