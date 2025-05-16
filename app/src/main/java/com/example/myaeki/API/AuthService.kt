package com.example.myaeki.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<UserResponse>

    @POST("/logout")
    fun logout(): Call<LogoutResponse>
}