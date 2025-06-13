package com.example.myaeki.Product.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myaeki.R
import com.example.myaeki.SearchFragment
import com.example.myaeki.API.ApiClient
import com.example.myaeki.Product.Model.ProductDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProductFragment : Fragment() {

    private lateinit var stockArrow: ImageView
    private var productId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_product, container, false)

        val backButton = view.findViewById<ImageView>(R.id.buttonBack)
        val searchButton = view.findViewById<ImageView>(R.id.buttonSearch)

        val productImage = view.findViewById<ImageView>(R.id.productImage)
        val productName = view.findViewById<TextView>(R.id.productSeries)
        val productPrice = view.findViewById<TextView>(R.id.productPrice)
        val productDescription = view.findViewById<TextView>(R.id.productDescription)

        stockArrow = view.findViewById(R.id.stockArrow)

        productId = arguments?.getInt("PRODUCT_ID") ?: -1
        Log.d("DetailProductFragment", "Received productId: $productId")

        if (productId != -1) {
            ApiClient.productService.getProductDetails(productId)
                .enqueue(object : Callback<ProductDetailResponse> {
                    override fun onResponse(call: Call<ProductDetailResponse>, response: Response<ProductDetailResponse>) {
                        if (response.isSuccessful) {
                            val product = response.body()?.product
                            if (product != null) {
                                productName.text = product.name
                                productPrice.text = " ${product.price.toInt()}"
                                productDescription.text = product.description

                                Glide.with(requireContext())
                                    .load(product.image_url)
                                    .into(productImage)
                            } else {
                                Log.w("API_RESPONSE", "Response body is null")
                            }
                        } else {
                            Log.w("API_RESPONSE", "Response not successful: ${response.code()} ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<ProductDetailResponse>, t: Throwable) {
                        Log.e("API_ERROR", "Failed to fetch product", t)
                    }
                })
        } else {
            Log.e("DetailProductFragment", "Invalid productId")
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        searchButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, SearchFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
