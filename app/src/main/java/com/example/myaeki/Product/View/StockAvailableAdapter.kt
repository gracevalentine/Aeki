package com.example.myaeki.Product.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.R


data class StoreItem(val storeName: String, val stockAvailable: Int, val isAvailable: Boolean)

class StockAvailableAdapter(private val storeList: List<StoreItem>) :
    RecyclerView.Adapter<StockAvailableAdapter.StoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val storeItem = storeList[position]
        holder.storeName.text = storeItem.storeName

        if (storeItem.isAvailable) {
            // Menggunakan string resource untuk stok tersedia
            holder.stockAvailability.text = String.format(
                holder.itemView.context.getString(R.string.available_in_store),
                storeItem.stockAvailable
            )
        } else {
            // Menggunakan string resource untuk stok kosong
            holder.stockAvailability.text = String.format(
                holder.itemView.context.getString(R.string.out_of_stock),
                storeItem.storeName
            )
        }
    }

    override fun getItemCount(): Int = storeList.size

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val stockAvailability: TextView = itemView.findViewById(R.id.stockAvailability)
    }
}
