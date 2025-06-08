package com.example.myaeki.Transaction.Model

data class CartItem(
    val cart_id: Int,
    val product_id: Int,
    val product_name: String,
    val product_price: Double,
    val quantity: Int,
    val stock_quantity: Int
)
