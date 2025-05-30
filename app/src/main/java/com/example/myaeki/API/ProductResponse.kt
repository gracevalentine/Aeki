package com.example.myaeki.API

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double
)

data class ProductResponse(
    val message: String,
    val products: List<Product>
)
