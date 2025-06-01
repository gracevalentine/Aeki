package com.example.myaeki

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val quantity: Int,
    val category: String?,
    val imageUrl: String? // penting: properti ini harus ada!
)