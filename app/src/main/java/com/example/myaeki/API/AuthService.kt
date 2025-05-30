package com.example.myaeki.API

import com.example.myaeki.api.LoginRequest
import com.example.myaeki.api.LogoutResponse
import com.example.myaeki.api.SignupRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<UserResponse>

    @POST("/logout")
    fun logout(): Call<LogoutResponse>

    @POST("/users/signup")
    fun signup(@Body request: SignupRequest): Call<UserResponse>

}