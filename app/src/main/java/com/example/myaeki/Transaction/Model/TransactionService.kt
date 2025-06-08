package com.example.myaeki.Transaction.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionService {
    @POST("/transaction/transactionProduct")
    fun transactionProduct(@Body request: TransactionProductRequest): Call<TransactionProductResponse>

    @POST("/transaction/transactionCart")
    fun transactionCart(@Body request: TransactionCartRequest): Call<TransactionCartResponse>

    @POST("/transaction/cart")
    fun addToCart(@Body request: TransactionCartRequest): Call<TransactionCartResponse>

    @GET("/transaction/cart/{userId}")
    fun getCartByUserId(@Path("userId") userId: Int): Call<List<CartItem>>
}

