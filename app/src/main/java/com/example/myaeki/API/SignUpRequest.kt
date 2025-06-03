package com.example.myaeki.API

data class SignupRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val address: String,
    val postal_code: Int,
    val email_checkbox: Boolean = false
)