package com.example.myaeki.Wallet.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletService {
    @POST("/wallet/topup")
    fun topUp(@Body request: TopUpRequest): Call<TopUpResponse>
}