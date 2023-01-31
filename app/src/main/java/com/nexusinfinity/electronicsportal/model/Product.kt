package com.nexusinfinity.electronicsportal.model

data class Product(
    val productId: String,
    val productModel: String,
    val productName: String,
    val productQnt: String,
    val productTarget: String,
    val sold: String = "5",
    val buyingPrice: String,
    val sellingPrice: String,
)