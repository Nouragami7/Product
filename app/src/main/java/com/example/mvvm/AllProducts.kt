package com.example.mvvm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mvvm.local.ProductDatabase
import com.example.mvvm.local.ProductLocalDataSource
import com.example.mvvm.model.Product
import com.example.mvvm.remote.ApiService
import com.example.mvvm.remote.ProductRemoteDataSource
import com.example.mvvm.remote.ResponseState
import com.example.mvvm.remote.RetrofitHelper
import com.example.mvvm.repo.ProductRepositoryImpl
import kotlinx.coroutines.launch

class AllProducts : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val factory = AllProductFactory(
                ProductRepositoryImpl.getInstance(
                    ProductRemoteDataSource(RetrofitHelper.retrofitInstance.create(ApiService::class.java)),
                    ProductLocalDataSource(ProductDatabase.getDatabase(this).productDao())
                )
            )
            val viewModel: AllProductsViewModel = viewModel(factory = factory)
            AllProductsScreen(viewModel)
        }
    }
}

@Composable
fun StarRating(rating: Double?, maxStars: Int = 5) {
    Row(modifier = Modifier.padding(top = 4.dp)) {
        for (i in 1..maxStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                tint = if (rating != null && i <= rating) Color(0xFFFFD700) else Color(0xFFB0B0B0),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductRow(product: Product, actionName: String, action: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            GlideImage(
                model = product.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.title ?: "No title", fontSize = 18.sp, color = Color.Black)
                Text(text = product.brand ?: "No brand", fontSize = 14.sp, color = Color.Gray)
                StarRating(rating = product.rating)
                Text(
                    text = product.description ?: "No description",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = action, modifier = Modifier.align(Alignment.End)) {
                    Text(text = actionName)
                }
            }
        }
    }
}

@Composable
fun AllProductsScreen(viewModel: AllProductsViewModel) {
    val productState by viewModel.products.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAllOnlineProducts()
    }

    when (productState) {
        is ResponseState.Loading -> LoadingIndicator()
        is ResponseState.Failure ->
            Toast.makeText(
                context,
                "An error occurred: ${(productState as ResponseState.Failure)}",
                Toast.LENGTH_SHORT
            ).show(
            )

        is ResponseState.Success -> ViewProduct(
            (productState as ResponseState.Success).data,
            "Add to favourites",
            viewModel,
            snackbarHostState
        )
    }
}

@Composable
fun ViewProduct(
    data: List<Product>,
    actionName: String,
    viewModel: AllProductsViewModel,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            items(data) { product ->
                ProductRow(product, actionName) {
                    viewModel.addProductToFavourite(product)
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}
