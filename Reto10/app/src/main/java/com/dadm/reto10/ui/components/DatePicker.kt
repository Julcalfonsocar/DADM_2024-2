package com.dadm.reto10.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    label: String,
    fecha: LocalDate,
    onFechaSeleccionada: (LocalDate) -> Unit,
    fechaMinima: LocalDate? = null,
    fechaMaxima: LocalDate? = null
) {
    val openDialog = remember { mutableStateOf(false) }
    val hoy = remember { LocalDate.now() }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val fechaSeleccionada = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                
                // No permitir fechas futuras
                if (fechaSeleccionada.isAfter(hoy)) {
                    return false
                }
                
                // Validar fecha mínima si existe
                if (fechaMinima != null && fechaSeleccionada.isBefore(fechaMinima)) {
                    return false
                }
                
                // Validar fecha máxima si existe
                if (fechaMaxima != null && fechaSeleccionada.isAfter(fechaMaxima)) {
                    return false
                }
                
                return true
            }
        }
    )
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.25f)
        )
        Text(
            text = fecha.format(dateFormatter),
            modifier = Modifier.weight(0.35f)
        )
        Button(
            onClick = { openDialog.value = true },
            modifier = Modifier.weight(0.35f),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text(
                text = "Seleccionar",
                maxLines = 1
            )
        }
    }

    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = { openDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val nuevaFecha = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onFechaSeleccionada(nuevaFecha)
                    }
                    openDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
} 