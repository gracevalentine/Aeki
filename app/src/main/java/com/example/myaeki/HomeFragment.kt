package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.Product.View.DetailProductFragment
import com.example.myaeki.wallet.view.TopUpFragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchButton = view.findViewById<ImageView>(R.id.searchButtoninHome)
        val topUpButton = view.findViewById<ImageButton>(R.id.topUpButton)

        val imageProduct1 = view.findViewById<ImageView>(R.id.imageProduct1)
        val productName1 = view.findViewById<TextView>(R.id.productName1)
        val productDescription1 = view.findViewById<TextView>(R.id.productDescription1)
        val productPrice1 = view.findViewById<TextView>(R.id.productPrice1)

        imageProduct1.setOnClickListener {
//            Toast.makeText(requireContext(), "Kamu klik ${productName1.text}", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, DetailProductFragment())
                .addToBackStack(null)
                .commit()
        }

        topUpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, TopUpFragment())
                .addToBackStack(null)
                .commit()
        }

        searchButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, SearchFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
