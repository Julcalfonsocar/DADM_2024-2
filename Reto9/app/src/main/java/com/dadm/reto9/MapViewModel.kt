// MapViewModel.kt
package com.dadm.reto9

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _pointsOfInterest = MutableStateFlow<List<PointOfInterest>>(emptyList())
    val pointsOfInterest: StateFlow<List<PointOfInterest>> = _pointsOfInterest.asStateFlow()

    private val _searchRadius = MutableStateFlow(5f)
    val searchRadius: StateFlow<Float> = _searchRadius.asStateFlow()

    // Base de datos de POIs en Bogotá
    private val bogotaPOIs = listOf(
        PointOfInterest(
            "Parque Estructurante La Gaitana",
            4.740998973677136,
            -74.10906218555964,
            POIType.Parque
        ),
        PointOfInterest(
            "Parque Fontanar del Rio",
            4.756295449266175,
            -74.11127177587787,
            POIType.Parque
        ),
        PointOfInterest(
            "Centro Comercial Portal 80",
            4.71116132693144,
            -74.11190844865656,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Centro Comercial Diverplaza",
            4.702237613661376,
            -74.11512620518937,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Centro Comercial Titán Plaza",
            4.695877414814781,
            -74.08613109827562,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Centro Comercial Portal de Hatochico",
            4.754941222876942,
            -74.10684023142964,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Centro Comercial Subazar",
            4.738857746311349,
            -74.08513107587801,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Parque Mirador de los Nevados",
            4.741831,
            -74.079674,
            POIType.Parque
        ),
        PointOfInterest(
            "Hospital de Suba",
            4.753816093485889,
            -74.09167100286285,
            POIType.Hospital
        ),
        PointOfInterest(
            "Parque Central de Suba",
            4.741398990434376,
            -74.08405543354965,
            POIType.Parque
        ),
        PointOfInterest(
            "Portal Suba",
            4.747004,
            -74.094359,
            POIType.Transporte
        ),
        PointOfInterest(
            "Parque La Gaitana",
            4.7361,
            -74.0876,
            POIType.Parque
        ),
        PointOfInterest(
            "Supermecado Éxito Suba",
            4.748338999400206,
            -74.09728459122134,
            POIType.Supermecado
        ),
        PointOfInterest(
            "Hospital Santa Fe",
            4.6919,
            -74.0445,
            POIType.Hospital
        ),
        PointOfInterest(
            "Centro Comercial Andino",
            4.6669,
            -74.0529,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Parque Simón Bolívar",
            4.6577,
            -74.0939,
            POIType.Parque
        ),
        PointOfInterest(
            "Museo Nacional",
            4.6161,
            -74.0694,
            POIType.Museo
        ),
        PointOfInterest(
            "Hospital Universitario San Ignacio",
            4.6278,
            -74.0652,
            POIType.Hospital
        ),
        PointOfInterest(
            "Parque El Virrey",
            4.6719,
            -74.0539,
            POIType.Parque
        ),
        PointOfInterest(
            "Museo del Oro",
            4.6019,
            -74.0708,
            POIType.Museo
        ),
        PointOfInterest(
            "Centro Comercial Santa Fe",
            4.7626,
            -74.0459,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Jardín Botánico",
            4.6697,
            -74.1018,
            POIType.Parque
        ),
        PointOfInterest(
            "Hospital Militar Central",
            4.6341,
            -74.0650,
            POIType.Hospital
        ),
        PointOfInterest(
            "Museo Botero",
            4.5972,
            -74.0714,
            POIType.Museo
        ),
        PointOfInterest(
            "Plaza de Bolívar",
            4.5981,
            -74.0758,
            POIType.Parque
        ),
        PointOfInterest(
            "Cerro de Monserrate",
            4.6057,
            -74.0557,
            POIType.Parque
        ),
        PointOfInterest(
            "Centro Internacional",
            4.6565,
            -74.0567,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Parque de la 93",
            4.6775,
            -74.0522,
            POIType.Parque
        ),
        PointOfInterest(
            "Hospital San José",
            4.6100,
            -74.0686,
            POIType.Hospital
        ),
        PointOfInterest(
            "Museo de Arte Moderno de Bogotá",
            4.6361,
            -74.0664,
            POIType.Museo
        ),
        PointOfInterest(
            "Centro Comercial Unicentro",
            4.6694,
            -74.0567,
            POIType.CentroComercial
        ),
        PointOfInterest(
            "Parque Nacional Enrique Olaya Herrera",
            4.6486,
            -74.0633,
            POIType.Parque
        ),
        PointOfInterest(
            "Hospital de La Misericordia",
            4.6108,
            -74.0658,
            POIType.Hospital
        )
    )

    fun updateSearchRadius(radius: Float) {
        _searchRadius.value = radius
        _currentLocation.value?.let { searchPointsOfInterest(it) }
    }

    fun startLocationUpdates(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                10f
            ) { location ->
                _currentLocation.value = location
                searchPointsOfInterest(location)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun searchPointsOfInterest(location: Location) {
        val radiusInKm = _searchRadius.value
        _pointsOfInterest.value = bogotaPOIs.filter { poi ->
            calculateDistance(
                location.latitude, location.longitude,
                poi.latitude, poi.longitude
            ) <= radiusInKm
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radio de la Tierra en kilómetros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}