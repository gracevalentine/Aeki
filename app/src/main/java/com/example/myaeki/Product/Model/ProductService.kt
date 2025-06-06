package com.example.myaeki.Product.Model

import com.example.myaeki.Authentication.Model.UserProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/products/searchProduct")
    fun searchProducts(@Query("productName") productName: String): Call<ProductResponse>
}