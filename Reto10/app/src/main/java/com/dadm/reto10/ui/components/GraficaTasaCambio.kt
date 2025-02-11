package com.dadm.reto10.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.dadm.reto10.R
import com.dadm.reto10.data.TasaCambio
import java.text.SimpleDateFormat
import java.util.Locale

class CustomMarkerView(
    context: Context,
    private val textColor: Int
) : MarkerView(context, R.layout.marker_view) {
    private val tvContent: TextView = findViewById(R.id.tvContent)
    
    init {
        tvContent.setTextColor(textColor)
    }

    fun setContent(text: String) {
        tvContent.text = text
    }
}

@Composable
fun GraficaTasaCambio(
    tasasCambio: List<TasaCambio>,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.WHITE else Color.BLACK
    val lineColor = if (isDarkTheme) Color.rgb(144, 202, 249) else Color.rgb(25, 118, 210)
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                description.apply {
                    isEnabled = false
                    textSize = 14f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                setExtraOffsets(20f, 30f, 20f, 10f)
                
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                xAxis.apply {
                    setDrawLabels(true)
                    position = XAxis.XAxisPosition.BOTTOM
                    setLabelCount(2, true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return try {
                                if (value == 0f) {
                                    tasasCambio.firstOrNull()?.let { tasa ->
                                        val date = inputFormat.parse(tasa.fecha)
                                        date?.let { dateFormatter.format(it) } ?: ""
                                    } ?: ""
                                } else if (value == (tasasCambio.size - 1).toFloat()) {
                                    tasasCambio.lastOrNull()?.let { tasa ->
                                        val date = inputFormat.parse(tasa.fecha)
                                        date?.let { dateFormatter.format(it) } ?: ""
                                    } ?: ""
                                } else ""
                            } catch (e: Exception) {
                                ""
                            }
                        }
                    }
                    setTextColor(textColor)
                    setGridColor(Color.GRAY)
                    gridLineWidth = 0.5f
                }

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        try {
                            e?.let { entry ->
                                val index = entry.x.toInt()
                                if (index in tasasCambio.indices) {
                                    val tasa = tasasCambio[index]
                                    val date = inputFormat.parse(tasa.fecha)
                                    val fechaFormateada = date?.let { dateFormatter.format(it) } ?: ""
                                    description.apply {
                                        text = "$fechaFormateada → $${String.format("%.2f", tasa.valor)}"
                                        textSize = 14f
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                        setTextColor(textColor)
                                        isEnabled = true
                                        setPosition(width / 2f, height * 0.1f)
                                    }
                                    invalidate()
                                }
                            }
                        } catch (e: Exception) {
                            description.isEnabled = false
                        }
                    }

                    override fun onNothingSelected() {
                        description.isEnabled = false
                        invalidate()
                    }
                })

                if (isDarkTheme) {
                    setBackgroundColor(Color.TRANSPARENT)
                    axisLeft.apply {
                        setTextColor(textColor)
                        setGridColor(Color.GRAY)
                        gridLineWidth = 0.5f
                    }
                    axisRight.isEnabled = false
                    legend.isEnabled = false
                }
            }
        },
        update = { chart ->
            try {
                val entries = tasasCambio.mapIndexed { index, tasa ->
                    Entry(index.toFloat(), tasa.valor.toFloat())
                }

                val dataSet = LineDataSet(entries, "TRM").apply {
                    setColor(lineColor)
                    setCircleColor(lineColor)
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawCircleHole(true)
                    circleHoleRadius = 2f
                    setDrawValues(false)
                    setDrawFilled(true)
                    setFillColor(lineColor)
                    fillAlpha = 50
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setHighLightColor(if (isDarkTheme) Color.WHITE else Color.BLACK)
                }

                chart.data = LineData(dataSet)
                chart.animateX(1000)
                chart.invalidate()
            } catch (e: Exception) {
                // Manejar el error silenciosamente
            }
        }
    )
} 