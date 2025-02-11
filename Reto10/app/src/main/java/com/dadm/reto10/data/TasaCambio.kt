package com.dadm.reto10.data

import com.google.gson.annotations.SerializedName

data class TasaCambio(
    @SerializedName("vigenciadesde")
    val fecha: String,
    @SerializedName("valor")
    val valor: Double
) 