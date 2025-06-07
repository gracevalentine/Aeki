package com.example.myaeki.Transaction.Model

class TransactionCartRequest(
    val user_id: Int,
    val cart_items: List<CartItem>
)

class CartItem(
    val product_id: Int,
    val quantity: Int
)
