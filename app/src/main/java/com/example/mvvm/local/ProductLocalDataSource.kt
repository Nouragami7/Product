package com.example.mvvm.local

import com.example.mvvm.model.Product
import kotlinx.coroutines.flow.Flow

class ProductLocalDataSource(private val productDao: ProductDAO) {
     fun getAllFavouriteProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }
    suspend fun insertProducts(product: Product) {
        productDao.insertProducts(product)

    }
    suspend fun deleteAllProducts(id: Int) {
        productDao.deleteProductById(id)
    }


}