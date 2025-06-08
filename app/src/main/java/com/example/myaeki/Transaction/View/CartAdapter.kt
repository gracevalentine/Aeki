package com.example.myaeki.Transaction.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.Product.Model.Product
import com.example.myaeki.R

class CartAdapter(
    private val products: List<Product>,
    private val onPlusClick: (Product) -> Unit,
    private val onMinusClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val quantityText: TextView = itemView.findViewById(R.id.textQuantity)
        val plusButton: ImageButton = itemView.findViewById(R.id.buttonPlus)
        val minusButton: ImageButton = itemView.findViewById(R.id.buttonMinus)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productPrice.text = "Rp ${String.format("%,.0f", product.price)}"
        holder.quantityText.text = product.stock_quantity.toString()

        holder.plusButton.setOnClickListener { onPlusClick(product) }
        holder.minusButton.setOnClickListener { onMinusClick(product) }
        holder.deleteButton.setOnClickListener { onDeleteClick(product) }
    }
}
