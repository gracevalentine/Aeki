package com.example.myaeki.Product.Model

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val quantity: Int,
    val category: String?,
    val imageUrl: String?
)