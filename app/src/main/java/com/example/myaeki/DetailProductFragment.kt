package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailProductFragment : Fragment() {

    private lateinit var stockArrow: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_product, container, false)

        stockArrow = view.findViewById(R.id.stockArrow)
        stockArrow.setOnClickListener {
            showStoreBottomSheet()
        }

        return view
    }

    private fun showStoreBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_store_stock, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)

        val closeBtn = bottomSheetView.findViewById<ImageView>(R.id.btnClose)
        closeBtn.setOnClickListener { dialog.dismiss() }

        val stockContainer = bottomSheetView.findViewById<LinearLayout>(R.id.stockContainer)

        // Contoh data dummy, nanti bisa kamu ganti dengan data asli dari database atau ViewModel
        val storeList = listOf(
            Pair("Toko A", "Stok: 12"),
            Pair("Toko B", "Stok: 0"),
            Pair("Toko C", "Stok: 4")
        )

        for ((storeName, stockInfo) in storeList) {
            val itemView = layoutInflater.inflate(R.layout.item_store, stockContainer, false)
            itemView.findViewById<TextView>(R.id.storeName).text = storeName
            itemView.findViewById<TextView>(R.id.stockAvailability).text = stockInfo
            stockContainer.addView(itemView)
        }

        dialog.show()
    }
}
