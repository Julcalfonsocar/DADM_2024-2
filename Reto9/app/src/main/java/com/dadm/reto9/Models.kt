package com.dadm.reto9

data class PointOfInterest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: POIType
)

enum class POIType {
    Hospital,
    Parque,
    Museo,
    Restaurante,
    CentroComercial,
    Iglesia,
    Educacion,
    Supermecado,
    Transporte
}