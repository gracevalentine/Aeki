package com.example.myaeki

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class ProductAdapter(private val listProduct: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.imageProduct1)
        val nama = itemView.findViewById<TextView>(R.id.productName1)
        val desc = itemView.findViewById<TextView>(R.id.productDescription1)
        val harga = itemView.findViewById<TextView>(R.id.productPrice1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        val product = listProduct[position]
//
//        // Load image pakai Glide
//        Glide.with(holder.itemView.context)
//            .load(product.imageUrl) // imageUrl harus URL yang valid ya
//            .placeholder(R.drawable.oftast) // gambar default kalau gagal
//            .into(holder.img)
//
//        holder.nama.text = product.name
//        holder.desc.text = product.description ?: "-"
//
//        // Format harga pakai Rupiah
//        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//        holder.harga.text = formatter.format(product.price)
    }

    override fun getItemCount(): Int = listProduct.size
}