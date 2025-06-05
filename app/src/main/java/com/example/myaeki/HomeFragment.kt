package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myaeki.Product.Model.ProductResponse
import com.example.myaeki.API.RetrofitClient
import com.example.myaeki.Product.View.DetailProductFragment


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
        val topUpButton = view.findViewById<LinearLayout>(R.id.sectionTopUpButton)

        val imageProduct1 = view.findViewById<ImageView>(R.id.imageProduct1)
        val productName1 = view.findViewById<TextView>(R.id.productName1)
        val productDescription1 = view.findViewById<TextView>(R.id.productDescription1)
        val productPrice1 = view.findViewById<TextView>(R.id.productPrice1)

//        val imageProduct2 = view.findViewById<ImageView>(R.id.imageProduct2)
//        val productName2 = view.findViewById<TextView>(R.id.productName2)
//        val productDescription2 = view.findViewById<TextView>(R.id.productDescription2)
//        val productPrice2 = view.findViewById<TextView>(R.id.productPrice2)
//
//        val imageProduct3 = view.findViewById<ImageView>(R.id.imageProduct3)
//        val productName3 = view.findViewById<TextView>(R.id.productName3)
//        val productDescription3 = view.findViewById<TextView>(R.id.productDescription3)
//        val productPrice3 = view.findViewById<TextView>(R.id.productPrice3)

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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    RetrofitClient.instance.searchProducts(query).enqueue(object : retrofit2.Callback<ProductResponse> {
                        override fun onResponse(
                            call: retrofit2.Call<ProductResponse>,
                            response: retrofit2.Response<ProductResponse>
                        ) {
                            if (response.isSuccessful) {
                                val products = response.body()?.products
                                if (!products.isNullOrEmpty()) {
                                    // Tampilkan hasil pertama (untuk sementara)
                                    val product = products[0]
                                    productName1.text = product.name
                                    productDescription1.text = product.description
                                    productPrice1.text = "Rp ${product.price}"

                                    // TODO: tampilkan product2 dan product3
                                } else {
                                    Toast.makeText(requireContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<ProductResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }
        })
    }
}
