package com.example.myaeki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewProduk)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val dummyProducts = listOf(
            Product(
                id = 1,
                name = "OFTAST",
                description = "Piring kecil",
                price = 9900.0,
                quantity = 10,
                category = "Dapur",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Product(
                id = 2,
                name = "FÄRGKLAR",
                description = "Piring hitam (4 pcs)",
                price = 149000.0,
                quantity = 15,
                category = "Dapur",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Product(
                id = 3,
                name = "OFTAST",
                description = "Piring cekung",
                price = 9900.0,
                quantity = 5,
                category = "Dapur",
                imageUrl = "https://via.placeholder.com/150"
            )
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
