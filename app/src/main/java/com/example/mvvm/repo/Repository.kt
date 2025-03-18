package com.example.mvvm.repo

import com.example.mvvm.model.Product
import kotlinx.coroutines.flow.Flow

interface Repository{
    suspend fun getAllOnlineProducts():Flow<List<Product>>
    suspend fun getAllOfflineProducts(): Flow<List<Product>>
    suspend fun insertProducts(product: Product)
    suspend fun deleteProductById(id: Int)

}

