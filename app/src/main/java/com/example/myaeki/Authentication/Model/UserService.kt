package com.example.myaeki.Authentication.Model


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("/users/profile/{id}")
    fun getUserProfile(@Path("id") userId: String): Call<UserProfileResponse>
}
