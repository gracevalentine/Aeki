package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val backButton = view.findViewById<ImageView>(R.id.buttonBack)
        backButton.setOnClickListener {
            // Kembali ke fragment sebelumnya di backstack
            parentFragmentManager.popBackStack()
        }
//        return
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.productRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val dummyProducts = listOf(
            Product(1, "OFTAST", "Piring kecil", 9900.0, 10, "Dapur", "https://via.placeholder.com/150"),
            Product(2, "FÄRGKLAR", "Piring hitam (4 pcs)", 149000.0, 15, "Dapur", "https://via.placeholder.com/150"),
            Product(3, "OFTAST", "Piring cekung", 9900.0, 5, "Dapur", "https://via.placeholder.com/150")
        )

//        NOTE: ENIH ADA CODE RETORFIT, MOGA MEMBANTU
//        ApiClient.instance.getAllProducts().enqueue(object : Callback<List<Product>> {
//            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
//                if (response.isSuccessful) {
//                    val productList = response.body() ?: emptyList()
//                    recyclerView.adapter = ProductAdapter(productList)
//                } else {
//                    Toast.makeText(requireContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
//                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
//            }
//        })

        recyclerView.adapter = ProductAdapter(dummyProducts) //NTAR INI ILANGIN AJA KLO PAKE RETRO
    }
}
