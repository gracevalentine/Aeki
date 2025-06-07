package com.example.myaeki.Product.Model

data class Product(
    val product_id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock_quantity: Int,
    val category: String?,
    val image_url: String?
)