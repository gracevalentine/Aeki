package com.example.myaeki

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Product.Model.ProductResponse
import com.example.myaeki.Product.View.DetailProductFragment
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

        adapter = ProductAdapter(
            emptyList(),
            onItemClick = { selectedProduct ->
                val detailFragment = DetailProductFragment()
                val bundle = Bundle().apply {
                    putInt("PRODUCT_ID", selectedProduct.product_id ?: -1)
                }
                detailFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onAddToCartClick = { product ->
                val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", android.content.Context.MODE_PRIVATE)
                val userIdStr = sharedPref.getString("USER_ID", null)
                val userId = userIdStr?.toIntOrNull() ?: -1

                if (userId == -1) {
                    Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
                    return@ProductAdapter
                }

                val request = com.example.myaeki.Transaction.Model.TransactionCartRequest(
                    user_id = userId,
                    product_id = product.product_id,
                    quantity = 1 // default 1
                )

                ApiClient.transactionService.addToCart(request).enqueue(object : retrofit2.Callback<com.example.myaeki.Transaction.Model.TransactionCartResponse> {
                    override fun onResponse(
                        call: Call<com.example.myaeki.Transaction.Model.TransactionCartResponse>,
                        response: Response<com.example.myaeki.Transaction.Model.TransactionCartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val message = response.body()?.message ?: "Berhasil ditambahkan ke cart"
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("SearchFragment", "Add to cart error body: $errorBody")
                            val msg = try {
                                JSONObject(errorBody ?: "").getString("message")
                            } catch (e: Exception) {
                                "Gagal menambahkan ke cart"
                            }
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<com.example.myaeki.Transaction.Model.TransactionCartResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        )

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        searchProductInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    Log.d("SearchFragment", "Query submitted: $query")
                    ApiClient.productService.searchProducts(query.trim())
                        .enqueue(object : Callback<ProductResponse> {
                            override fun onResponse(
                                call: Call<ProductResponse>,
                                response: Response<ProductResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val products = response.body()?.products
                                    adapter.updateProducts(products ?: emptyList())
                                    if (products.isNullOrEmpty()) {
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
                                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
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
