package com.example.myaeki.Transaction.Model

data class CheckoutRequest(
    val user_id: Int,
    val product_id: Int,
    val quantity: Int
)

