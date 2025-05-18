package com.example.myaeki.api

data class UserResponse(
val message: String,
val user: User?
)

data class User(
val user_id: Int,
val username: String,
val email: String
)