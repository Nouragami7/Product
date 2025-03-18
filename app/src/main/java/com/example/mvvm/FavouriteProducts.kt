package com.example.mvvm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

class FavouriteProducts : ComponentActivity() {
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
            AllFavouriteProduct(viewModel)
        }
    }


    /* fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }
}*/

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun FavProductRow(product: Product, actionName: String, action: () -> Unit) {
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
    fun AllFavouriteProduct(viewModel: AllProductsViewModel) {
        val productState by viewModel.favproducts.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.toastEvent.collect { message ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            viewModel.getAllOfflineProducts()
        }

        when (productState) {
            is ResponseState.Loading -> LoadingIndicator()
            is ResponseState.Failure ->
                Toast.makeText(
                    context,
                    "An error occurred: ${(productState as ResponseState.Failure)}",
                    Toast.LENGTH_SHORT
                ).show()
            is ResponseState.Success ->
                ViewFavProduct(
                    (productState as ResponseState.Success).data,
                    "Delete from favourites",
                    viewModel,
                    snackbarHostState)
        }



        /* Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn {
                items(productState.value?.size ?: 0) {
                    productState.value?.get(it)?.let { it1 ->
                        FavProductRow(it1, "Delete from favourites") {
                            viewModel.deleteProductFromFavourite(it1)
                            scope.launch {
                                messageState.value?.let { message ->
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/
    }
}

@Composable
fun ViewFavProduct(
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
                    viewModel.getAllOfflineProducts()
                    viewModel.deleteProductFromFavourite(product)
                }
            }
        }
    }
}