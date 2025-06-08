package com.example.myaeki.Transaction.Model

data class BuyProductRequest(
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val deliveryMethod: String
)
