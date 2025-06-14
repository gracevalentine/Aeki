package com.example.myaeki.Product.Model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/products/searchProduct")
    fun searchProducts(@Query("name") name: String): Call<ProductResponse>

    @GET("/products/product/{id}")
    fun getProductDetails(@Path("id") id: Int): Call<ProductDetailResponse>

}