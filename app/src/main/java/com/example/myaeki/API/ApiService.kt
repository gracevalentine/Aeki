package com.example.myaeki.API
import com.example.myaeki.Product.Model.ProductResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

//    @GET("/products/searchProducts")
//    fun searchProducts(): Call<List<ProductResponse>>

    @GET("/products/searchProducts")
    fun searchProducts(@Query("query") query: String): Call<ProductResponse>


}
