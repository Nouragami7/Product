package com.example.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class Product(
    @PrimaryKey var id: Int,
     val title: String,
    val brand: String?,
    val thumbnail: String?,
    val description: String?,
    val rating: Double?
) {

}

data class Products(
    val products: List<Product>
)
