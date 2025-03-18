package com.example.mvvm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StartScreen(
                onNavigateToAllProducts = {
                    startActivity(Intent(this, AllProducts::class.java))
                },
                onNavigateToFavouriteProducts = {
                    startActivity(Intent(this, FavouriteProducts::class.java))
                }
            )
        }
    }
}
@Composable
fun StartScreen(onNavigateToAllProducts: () -> Unit, onNavigateToFavouriteProducts: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
       ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
        )
        Button(
            onClick = onNavigateToAllProducts,
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(text = "All products")
        }
        Button(
            onClick = onNavigateToFavouriteProducts,
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth(),

        ){
            Text(text = "Favorites")
        }
        Button(
            onClick = { },
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth(),
        ){
            Text(text = "Exit")
        }
    }
}