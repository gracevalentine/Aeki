package com.example.myaeki.api

data class UserResponse(
    val message: String,
    val user: User?
)

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String,
    val postal_code: Int
)
