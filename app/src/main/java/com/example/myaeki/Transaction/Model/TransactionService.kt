package com.example.myaeki.Transaction.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionService {
    @POST("/transaction/transactionProduct")
    fun transactionProduct(@Body request: TransactionProductRequest): Call<TransactionProductResponse>

    @POST("/transaction/transactionCart")
    fun transactionCart(@Body request: TransactionCartRequest): Call<TransactionCartResponse>
}

