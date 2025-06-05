package com.example.myaeki.Authentication.Model

data class LoginResponse(
    val message: String,
    val token: String?,
    val user: User?
)