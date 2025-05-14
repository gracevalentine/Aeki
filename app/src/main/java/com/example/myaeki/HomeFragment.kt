package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        val imageProduct1 = view.findViewById<ImageView>(R.id.imageProduct1)
        val productName1 = view.findViewById<TextView>(R.id.productName1)
        val productDescription1 = view.findViewById<TextView>(R.id.productDescription1)
        val productPrice1 = view.findViewById<TextView>(R.id.productPrice1)

        val imageProduct2 = view.findViewById<ImageView>(R.id.imageProduct2)
        val productName2 = view.findViewById<TextView>(R.id.productName2)
        val productDescription2 = view.findViewById<TextView>(R.id.productDescription2)
        val productPrice2 = view.findViewById<TextView>(R.id.productPrice2)

        val imageProduct3 = view.findViewById<ImageView>(R.id.imageProduct3)
        val productName3 = view.findViewById<TextView>(R.id.productName3)
        val productDescription3 = view.findViewById<TextView>(R.id.productDescription3)
        val productPrice3 = view.findViewById<TextView>(R.id.productPrice3)

        imageProduct1.setOnClickListener {
            Toast.makeText(requireContext(), "Kamu klik ${productName1.text}", Toast.LENGTH_SHORT).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(requireContext(), "Cari: $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}
