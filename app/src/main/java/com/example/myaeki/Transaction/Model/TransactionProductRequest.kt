package com.example.myaeki.Transaction.Model

data class TransactionProductRequest(
    val user_id: Int,
    val product_id: Int,
    val quantity: Int,
    val subtotal: Float
)
