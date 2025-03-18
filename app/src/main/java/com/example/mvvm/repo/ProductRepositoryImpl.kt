package com.example.mvvm.repo

import com.example.mvvm.local.ProductLocalDataSource
import com.example.mvvm.model.Product
import com.example.mvvm.remote.ProductRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl private constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource
) : Repository {
    override suspend fun getAllOnlineProducts(): Flow<List<Product>> {
        return remoteDataSource.getAllProducts()!!
    }

    override suspend fun getAllOfflineProducts(): Flow<List<Product>> {
        return localDataSource.getAllFavouriteProducts()
    }

    override suspend fun insertProducts(product: Product) {
        return localDataSource.insertProducts(product)
    }

    override suspend fun deleteProductById(id: Int) {
        return localDataSource.deleteAllProducts(id)
    }

    companion object {
        @Volatile
        private var instance: ProductRepositoryImpl? = null
        fun getInstance(
            remoteDataSource: ProductRemoteDataSource, localDataSource: ProductLocalDataSource
        ): ProductRepositoryImpl {
            return instance ?: synchronized(this) {
                val temp = ProductRepositoryImpl(remoteDataSource, localDataSource)
                instance = temp
                temp

            }
        }
    }
}

