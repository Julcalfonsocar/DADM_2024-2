package com.dadm.reto10.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dadm.reto10.data.TasaCambio
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TablaTasaCambio(
    tasasCambio: List<TasaCambio>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(modifier = modifier) {
        // Encabezado fijo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Text(
                text = "Fecha",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Valor",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Divider()

        // Contenido de la tabla con scroll
        LazyColumn {
            items(tasasCambio) { tasa ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val date = inputFormat.parse(tasa.fecha)
                    val fechaFormateada = date?.let { dateFormatter.format(it) } ?: tasa.fecha
                    
                    Text(
                        text = fechaFormateada,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = String.format("$%.2f", tasa.valor),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
                Divider()
            }
        }
    }
} 