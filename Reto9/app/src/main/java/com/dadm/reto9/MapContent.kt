package com.dadm.reto9

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import androidx.core.content.ContextCompat
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun MapContent(viewModel: MapViewModel) {
    val context = LocalContext.current
    val currentLocation by viewModel.currentLocation.collectAsState()
    val pointsOfInterest by viewModel.pointsOfInterest.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    var showRadiusDialog by remember { mutableStateOf(false) }
    var tempRadius by remember { mutableStateOf("") }

    // FocusRequester para enfocar el campo de texto automáticamente
    val focusRequester = remember { FocusRequester() }

    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    Box(modifier = Modifier.fillMaxSize()) {
        // Controles superiores con zIndex alto para mantenerlos visibles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Text(
                    "Radio actual: ${searchRadius.toInt()} km",
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = {
                    showRadiusDialog = true
                    tempRadius = searchRadius.toString()
                }
            ) {
                Text("Cambiar radio")
            }
        }

        // Mapa
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.0)
                }
            },
            update = { mapView ->
                currentLocation?.let { location ->
                    val currentPoint = GeoPoint(location.latitude, location.longitude)
                    mapView.controller.setCenter(currentPoint)

                    // Limpiar overlays anteriores
                    mapView.overlays.clear()

                    // Dibujar círculo del radio
                    val circle = Polygon().apply {
                        points = createCirclePoints(currentPoint, searchRadius)
                        fillColor = Color.argb(50, 0, 0, 255)
                        strokeColor = Color.BLUE
                        strokeWidth = 2f
                    }
                    mapView.overlays.add(circle)

                    // Agregar marcador de ubicación actual
                    val currentLocationMarker = Marker(mapView).apply {
                        position = currentPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_current_location)
                        title = "Mi ubicación"
                    }
                    mapView.overlays.add(currentLocationMarker)

                    // Agregar marcadores de puntos de interés
                    pointsOfInterest.forEach { poi ->
                        val poiMarker = Marker(mapView).apply {
                            position = GeoPoint(poi.latitude, poi.longitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            icon = ContextCompat.getDrawable(context, R.drawable.ic_marker)?.apply {
                                setBounds(0, 0, 48, 48)  // Hacemos el icono un poco más grande
                            }
                            title = poi.name
                            snippet = poi.type.name

                            // Configuramos el comportamiento al hacer clic
                            setOnMarkerClickListener { marker, mapView ->
                                marker.showInfoWindow()
                                true
                            }
                        }
                        mapView.overlays.add(poiMarker)
                    }

                    // Actualización del mapa
                    mapView.invalidate()
                }
            }
        )
    }

    // Dialog para ingresar el radio
    if (showRadiusDialog) {
        var textFieldValue by remember {
            mutableStateOf(TextFieldValue(tempRadius, TextRange(tempRadius.length)))
        }

        // Enfocar el campo de texto y mostrar el teclado automáticamente
        LaunchedEffect(showRadiusDialog) {
            if (showRadiusDialog) {
                focusRequester.requestFocus()
                textFieldValue = textFieldValue.copy(
                    selection = TextRange(0, textFieldValue.text.length)
                )
            }
        }

        AlertDialog(
            onDismissRequest = { showRadiusDialog = false },
            title = { Text("Establecer radio de búsqueda") },
            text = {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        tempRadius = newValue.text
                    },
                    label = { Text("Radio en kilómetros") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester) // Enfocar automáticamente
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                textFieldValue = textFieldValue.copy(
                                    selection = TextRange(0, textFieldValue.text.length)
                                )
                            }
                        }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        tempRadius.toFloatOrNull()?.let { radius ->
                            if (radius > 0) {
                                viewModel.updateSearchRadius(radius)
                                showRadiusDialog = false
                            }
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRadiusDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun createCirclePoints(center: GeoPoint, radiusKm: Float): ArrayList<GeoPoint> {
    val points = ArrayList<GeoPoint>()
    val earthRadius = 6371000.0 // Radio de la tierra en metros
    val radiusInMeters = radiusKm * 1000.0
    val numberOfPoints = 60 // Para un círculo más suave

    for (i in 0..numberOfPoints) {
        val bearing = (360.0 * i / numberOfPoints)
        val lat1 = Math.toRadians(center.latitude)
        val lon1 = Math.toRadians(center.longitude)
        val angularDistance = radiusInMeters / earthRadius

        val lat2 = Math.asin(
            Math.sin(lat1) * Math.cos(angularDistance) +
                    Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(Math.toRadians(bearing))
        )

        val lon2 = lon1 + Math.atan2(
            Math.sin(Math.toRadians(bearing)) * Math.sin(angularDistance) * Math.cos(lat1),
            Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2)
        )

        val latitudeFinal = Math.toDegrees(lat2)
        val longitudeFinal = Math.toDegrees(lon2)

        points.add(GeoPoint(latitudeFinal, longitudeFinal))
    }

    return points
}