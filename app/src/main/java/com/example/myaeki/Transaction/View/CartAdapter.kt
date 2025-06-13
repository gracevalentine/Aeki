package com.example.myaeki.Transaction.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.R
import com.example.myaeki.Transaction.Model.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onPlusClick: (CartItem) -> Unit,
    private val onMinusClick: (CartItem) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit
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

    override fun getItemCount() = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.productName.text = item.product_name
        holder.productPrice.text = "${String.format("%,.0f", item.product_price)}"
        holder.quantityText.text = item.quantity.toString()

        holder.plusButton.setOnClickListener { onPlusClick(item) }
        holder.minusButton.setOnClickListener { onMinusClick(item) }
        holder.deleteButton.setOnClickListener { onDeleteClick(item) }
    }
}
