package com.dadm.reto10.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dadm.reto10.data.TasaCambio
import com.dadm.reto10.network.TasaCambioService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TasaCambioViewModel : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.datos.gov.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(TasaCambioService::class.java)
    
    var tasasCambio by mutableStateOf<List<TasaCambio>>(emptyList())
        private set
        
    var fechaInicio by mutableStateOf(LocalDate.now().minusMonths(1))
        private set
        
    var fechaFin by mutableStateOf(LocalDate.now())
        private set
        
    var isLoading by mutableStateOf(false)
        private set
        
    var error by mutableStateOf<String?>(null)
        private set

    fun actualizarFechaInicio(fecha: LocalDate) {
        fechaInicio = fecha
        cargarDatos()
    }

    fun actualizarFechaFin(fecha: LocalDate) {
        fechaFin = fecha
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val query = "vigenciadesde between '${fechaInicio.format(DateTimeFormatter.ISO_DATE)}' and '${fechaFin.format(DateTimeFormatter.ISO_DATE)}'"
                tasasCambio = service.getTasaCambio(query)
                    .sortedBy { it.fecha }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
} 