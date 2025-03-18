package com.example.mvvm.remote

import com.example.mvvm.model.Product

sealed class ResponseState {
    data object Loading : ResponseState()
    data class Success(val data: List<Product>) : ResponseState()
    data class Failure(val msg: Throwable) : ResponseState()
}