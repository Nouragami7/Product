package com.example.mvvm.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvm.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(product: Product)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int)


}