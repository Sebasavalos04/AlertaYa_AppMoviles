package com.example.alertaya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.example.alertaya.ui.theme.*
import com.example.alertaya.datos.vistamodelo.ClimaViewModel
import com.example.alertaya.datos.vistamodelo.PronosticoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen() {
    val climaViewModel: ClimaViewModel = viewModel()
    val estadoClima = climaViewModel.clima.collectAsState().value

    val pronosticoViewModel: PronosticoViewModel = viewModel()
    val listaPronostico = pronosticoViewModel.pronostico.collectAsState().value

    LaunchedEffect(Unit) {
        if (estadoClima == null) {
            climaViewModel.cargarClima("Trujillo,PE", "2aa81b66e47c7560e89756278cc3ff2d")
        }
        if (listaPronostico.isEmpty()) {
            pronosticoViewModel.cargarPronostico("Trujillo,PE", "2aa81b66e47c7560e89756278cc3ff2d")
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (estadoClima != null) {
            Text(estadoClima.nombreCiudad, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Black)
            Text(obtenerFechaDesdeTimestamp(estadoClima.fechaUnix), fontSize = 14.sp, color = GrayDark)

            Spacer(modifier = Modifier.height(24.dp))

            SeccionAlerta()

            ClimaActual(
                temperatura = estadoClima.informacionPrincipal.temperatura,
                sensacion = estadoClima.informacionPrincipal.sensacionTermica,
                humedad = estadoClima.informacionPrincipal.humedad,
                presion = estadoClima.informacionPrincipal.presion,
                viento = estadoClima.viento.velocidad
            )

            val lluviaActual = listaPronostico.firstOrNull()?.lluvia?.cantidad3h ?: 0.0
            IntensidadLLuvia(lluviaActual)

            val listaLluvia = listaPronostico.take(8).map { it.lluvia?.cantidad3h ?: 0.0 }
            GraficoIntensidad(listaLluvia)

            Pronostico() // aún está con datos simulados
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun IntensidadLLuvia(lluviaActual: Double) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
        Text("Intensidad de Lluvia", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Black)
        Text("Actual: ${"%.1f".format(lluviaActual)} mm/h", fontSize = 14.sp, color = RedAlert)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (lluviaActual.toFloat() / 60f).coerceIn(0f, 1f),
            color = RedAlert,
            trackColor = GrayLight,
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))
        )
    }
}

@Composable
fun GraficoIntensidad(lluviasPorHora: List<Double>) {
    val entries = lluviasPorHora.mapIndexed { index, valor ->
        Entry(index.toFloat(), valor.toFloat())
    }

    AndroidView(factory = { context ->
        LineChart(context).apply {
            val dataSet = LineDataSet(entries, "Lluvia (mm)").apply {
                color = android.graphics.Color.RED
                valueTextColor = android.graphics.Color.BLACK
                lineWidth = 2f
                setDrawCircles(false)
            }
            data = LineData(dataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
        }
    }, modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 8.dp))
}

@Composable
fun ClimaActual(temperatura: Double, sensacion: Double, humedad: Int, presion: Int, viento: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🌧️", fontSize = 48.sp)

        Column(horizontalAlignment = Alignment.Start) {
            Text("${temperatura}°C", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Black)
            Text("Sensación térmica: ${sensacion}°C", fontSize = 14.sp, color = GrayDark)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text("Humedad: ${humedad}%", fontSize = 14.sp, color = GrayDark)
            Text("Viento: ${viento} km/h", fontSize = 14.sp, color = GrayDark)
            Text("Presión: ${presion} hPa", fontSize = 14.sp, color = GrayDark)
        }
    }
}

@Composable
fun SeccionAlerta() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(RedAlert.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("Alerta Actual: Alta", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RedAlert)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Lluvia intensa\nPosibles inundaciones en zonas bajas.\nSe recomienda evitar salir y mantenerse en lugares seguros.",
            fontSize = 14.sp, color = Black
        )
    }
}

@Composable
fun Pronostico() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TarjetaPronostico("Hoy", "23°C", "Alta", RedAlert)
            TarjetaPronostico("Mañana", "25°C", "Media", YellowMedium)
            TarjetaPronostico("Miércoles", "26°C", "Baja", BluePrimary)
            TarjetaPronostico("Jueves", "28°C", "Mínima", GreenSuccess)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Ver más", color = BluePrimary, fontSize = 14.sp)
    }
}

@Composable
fun TarjetaPronostico(dia: String, temp: String, nivel: String, color: Color) {
    Column(
        modifier = Modifier.width(80.dp)
            .background(color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(dia, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Black)
        Text(temp, fontSize = 14.sp, color = GrayDark)
        Text(nivel, fontSize = 14.sp, color = color)
    }
}

fun obtenerFechaDesdeTimestamp(timestamp: Long): String {
    val formato = SimpleDateFormat("EEEE, d 'de' MMMM, yyyy", Locale("es", "ES"))
    val fecha = Date(timestamp * 1000)
    return formato.format(fecha).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}






