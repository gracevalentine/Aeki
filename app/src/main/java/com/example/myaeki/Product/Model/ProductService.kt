package com.example.myaeki.Product.Model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products/searchProduct")
    fun searchProducts(
        @Query("productName") productName: String
    ): Call<ProductResponse>
}
