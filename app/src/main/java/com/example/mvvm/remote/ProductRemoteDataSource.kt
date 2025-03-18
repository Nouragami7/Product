package com.example.mvvm.remote

import com.example.mvvm.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class ProductRemoteDataSource(private val apiService: ApiService) {
    suspend fun getAllProducts(): Flow<List<Product>>? {
        return apiService.getProducts().body()?.let { flowOf(it.products) }
    }
}
