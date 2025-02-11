package com.dadm.reto10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dadm.reto10.ui.theme.Reto10Theme
import com.dadm.reto10.viewmodel.TasaCambioViewModel
import com.dadm.reto10.ui.screens.TasaCambioScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto10Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: TasaCambioViewModel = viewModel()
                    TasaCambioScreen(viewModel)
                }
            }
        }
    }
}