package com.example.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mvvm.model.Product
import com.example.mvvm.remote.ResponseState
import com.example.mvvm.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class AllProductsViewModel(private val repo: Repository) : ViewModel() {
    /*private val mutableProducts: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>> = mutableProducts

    private val mutableMessage: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = mutableMessage*/

    private val favMutableProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val favproducts = favMutableProducts.asStateFlow()

    private val mutableProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val products = mutableProducts.asStateFlow()


    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        getAllOnlineProducts()
    }


    fun getAllOnlineProducts() {
        viewModelScope.launch {
            try {
                val products = repo.getAllOnlineProducts()
                products.catch { e ->
                    mutableProducts.value = ResponseState.Failure(e)
                    _toastEvent.emit("An error occurred: ${e.message}")
                }.collect {
                    mutableProducts.value = ResponseState.Success(it)
                }
            } catch (e: Exception) {
                mutableProducts.value = ResponseState.Failure(e)
                _toastEvent.emit("An error occurred: ${e.message}")
            }
        }
    }

    fun getAllOfflineProducts() {
        viewModelScope.launch {
            try {
                val products = repo.getAllOfflineProducts()
                products.catch { e ->
                    favMutableProducts.value = ResponseState.Failure(e)
                    _toastEvent.emit("An error occurred: ${e.message}")
                }.collect {
                    favMutableProducts.value = ResponseState.Success(it)
                }
            } catch (e: Exception) {
                _toastEvent.emit("An error occurred: ${e.message}")
            }
        }
    }

    fun addProductToFavourite(product: Product?) {
        if (product != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = repo.insertProducts(product)
                    if (result != null) {
                        _toastEvent.emit("Product added to favourites")
                    } else {
                        _toastEvent.emit("Product not added to favourites")
                    }
                } catch (e: Exception) {
                    _toastEvent.emit("An error occurred: ${e.message}")
                }
            }
        }
    }

    fun deleteProductFromFavourite(product: Product?) {
        if (product != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = repo.deleteProductById(product.id)
                    getAllOfflineProducts()
                    if (result != null) {
                        _toastEvent.emit("Product deleted from favourites")
                    } else {
                        _toastEvent.emit("Product not deleted from favourites")
                    }
                } catch (e: Exception) {
                    _toastEvent.emit("An error occurred: ${e.message}")
                }
            }
        }
    }

}

class AllProductFactory(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllProductsViewModel(repo) as T
    }
}



