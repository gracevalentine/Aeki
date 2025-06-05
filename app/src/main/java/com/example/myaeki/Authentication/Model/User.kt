package com.example.myaeki.Authentication.Model


data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String,
    val postal_code: Int,
    val wallet: Int
)