package com.example.myaeki.Product.View

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.Product.Model.Product
import com.example.myaeki.R
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private var listProduct: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imageProduct)
        val nama: TextView = itemView.findViewById(R.id.productName1)
        val desc: TextView = itemView.findViewById(R.id.productDescription1)
        val harga: TextView = itemView.findViewById(R.id.productPrice1)
        val cartButton: ImageView = itemView.findViewById(R.id.addToCartButton)

        fun bind(product: Product) {
            nama.text = product.name ?: "-"
            desc.text = product.description ?: "-"

            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            formatter.maximumFractionDigits = 0
            harga.text = formatter.format(product.price ?: 0.0)

            itemView.setOnClickListener {
                onItemClick(product)
            }

            cartButton.setOnClickListener {
                onAddToCartClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        try {
            val product = listProduct[position]
            holder.bind(product)
        } catch (e: Exception) {
            Log.e("ProductAdapter", "Error binding product: ${e.message}")
        }
    }

    override fun getItemCount(): Int = listProduct.size

    fun updateProducts(newList: List<Product>) {
        listProduct = newList
        notifyDataSetChanged()
    }
}