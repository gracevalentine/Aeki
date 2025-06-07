package com.example.myaeki

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Product.Model.ProductResponse
import com.example.myaeki.Product.View.ProductAdapter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchProductInput: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val backButton = view.findViewById<ImageView>(R.id.buttonBack)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.productRecyclerView)
        searchProductInput = view.findViewById(R.id.searchProductInput)

        adapter = ProductAdapter(emptyList())
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        searchProductInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val searchQuery = query.trim()
                    Log.d("SearchFragment", "Query submitted: $searchQuery")

                    ApiClient.productService.searchProducts(searchQuery)
                        .enqueue(object : Callback<ProductResponse> {
                            override fun onResponse(
                                call: Call<ProductResponse>,
                                response: Response<ProductResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val products = response.body()?.products ?: emptyList()
                                    Log.d("SearchFragment", "Produk ditemukan: $products")

                                    // Update UI list produk di recyclerView
                                    adapter.updateProducts(products)

                                    if (products.isEmpty()) {
                                        Toast.makeText(requireContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val errorBodyStr = response.errorBody()?.string()
                                    Log.e("SearchFragment", "Error response: $errorBodyStr")

                                    val errorMsg = try {
                                        JSONObject(errorBodyStr ?: "").getString("message")
                                    } catch (e: Exception) {
                                        "Gagal mendapatkan data"
                                    }

                                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${t.message}",
                                    Toast.LENGTH_LONG
                                ).show()
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
