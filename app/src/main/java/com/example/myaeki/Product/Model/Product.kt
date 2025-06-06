package com.example.myaeki.Product.Model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("product_id")
    val id: Int,

    val name: String,

    val description: String?,

    val price: Double,

    @SerializedName("stock_quantity")
    val quantity: Int,

    val category: String?,

    @SerializedName("image_url")
    val imageUrl: String?
)