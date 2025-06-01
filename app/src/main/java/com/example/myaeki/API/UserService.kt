package com.example.myaeki.API


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("user/{id}") // sesuaikan dengan endpoint kamu di backend
    fun getUserProfile(@Path("id") userId: String): Call<UserProfile>
}
