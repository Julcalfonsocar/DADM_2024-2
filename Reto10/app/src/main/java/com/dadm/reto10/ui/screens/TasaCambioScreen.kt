package com.dadm.reto10.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dadm.reto10.viewmodel.TasaCambioViewModel
import com.dadm.reto10.ui.components.DatePicker
import com.dadm.reto10.ui.components.GraficaTasaCambio
import com.dadm.reto10.ui.components.TablaTasaCambio
import androidx.compose.ui.text.font.FontWeight
import com.dadm.reto10.ui.components.ErrorView

@Composable
fun TasaCambioScreen(viewModel: TasaCambioViewModel) {
    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 96.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Cambio TRM del Dolar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DatePicker(
                label = "Fecha Inicio",
                fecha = viewModel.fechaInicio,
                onFechaSeleccionada = viewModel::actualizarFechaInicio,
                fechaMaxima = viewModel.fechaFin
            )
            
            DatePicker(
                label = "Fecha Fin",
                fecha = viewModel.fechaFin,
                onFechaSeleccionada = viewModel::actualizarFechaFin,
                fechaMinima = viewModel.fechaInicio
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator()
                }
                viewModel.error != null -> {
                    val errorMessage = when {
                        viewModel.error!!.contains("Unable to resolve host") -> 
                            "No hay conexión a Internet.\nPor favor, verifica tu conexión y vuelve a intentar."
                        else -> "Error: ${viewModel.error}"
                    }
                    ErrorView(
                        message = errorMessage,
                        onRetry = { viewModel.cargarDatos() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    Column {
                        GraficaTasaCambio(
                            tasasCambio = viewModel.tasasCambio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TablaTasaCambio(
                            tasasCambio = viewModel.tasasCambio,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
} 