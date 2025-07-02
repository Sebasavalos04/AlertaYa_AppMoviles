package com.example.alertaya.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.NavController
import com.example.alertaya.data.model.response.PronosticoPorHora
import com.example.alertaya.datos.vistamodelo.AlertaViewModel
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userName: String,
    onLogout: () -> Unit
) {
    val climaViewModel: ClimaViewModel = viewModel()
    val estadoClima = climaViewModel.clima.collectAsState().value
    val alertaViewModel: AlertaViewModel = viewModel()
    val nivelAlerta: StateFlow<String> = alertaViewModel.nivelAlerta
    val recomendaciones: StateFlow<List<String>> = alertaViewModel.recomendaciones

    val pronosticoViewModel: PronosticoViewModel = viewModel()
    val listaPronostico = pronosticoViewModel.pronostico.collectAsState().value

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (estadoClima == null) {
            climaViewModel.cargarClima("Trujillo,PE", "2aa81b66e47c7560e89756278cc3ff2d")
        }
        if (listaPronostico.isEmpty()) {
            pronosticoViewModel.cargarPronostico("Trujillo,PE", "2aa81b66e47c7560e89756278cc3ff2d")
        }
        alertaViewModel.cargarAlerta("Trujillo,PE", "2aa81b66e47c7560e89756278cc3ff2d")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AlertaYa") },
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Usuario: $userName") },
                                onClick = { expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    expanded = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (estadoClima != null) {
                Text(estadoClima.nombreCiudad, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Black)
                Text(obtenerFechaDesdeTimestamp(estadoClima.fechaUnix), fontSize = 14.sp, color = GrayDark)

                Spacer(modifier = Modifier.height(24.dp))

                SeccionAlerta(viewModel = alertaViewModel)

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

                Pronostico(listaPronostico) // aún está con datos simulados
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
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
fun SeccionAlerta(viewModel: AlertaViewModel = viewModel()) {
    val nivelAlerta by viewModel.nivelAlerta.collectAsState()
    val recomendaciones by viewModel.recomendaciones.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Nivel de alerta: $nivelAlerta",
            style = MaterialTheme.typography.titleMedium,
            color = when (nivelAlerta) {
                "Fuerte" -> Color.Red
                "Moderada" -> Color(0xFFFFA000) // Ámbar
                "Suave" -> Color(0xFF1976D2) // Azul
                else -> Color(0xFF388E3C) // Verde
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        recomendaciones.forEach { recomendacion ->
            Text(
                text = "• $recomendacion",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
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
@Composable
fun Pronostico(listaPronostico: List<PronosticoPorHora>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listaPronostico.take(4).forEachIndexed { index, item ->
                val dia = when (index) {
                    0 -> "Hoy"
                    1 -> "Mañana"
                    2 -> "Miércoles"
                    3 -> "Jueves"
                    else -> "Día"
                }

                val temp = "${item.informacionPrincipal.temperatura.toInt()}°C"
                val nivel = when {
                    item.informacionPrincipal.temperatura >= 30 -> "Alta"
                    item.informacionPrincipal.temperatura >= 25 -> "Media"
                    item.informacionPrincipal.temperatura >= 20 -> "Baja"
                    else -> "Mínima"
                }

                val color = when (nivel) {
                    "Alta" -> RedAlert
                    "Media" -> YellowMedium
                    "Baja" -> BluePrimary
                    else -> GreenSuccess
                }

                    TarjetaPronostico(dia, temp, nivel, color)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Ver más", color = BluePrimary, fontSize = 14.sp)
    }
}



fun obtenerFechaDesdeTimestamp(timestamp: Long): String {
    val formato = SimpleDateFormat("EEEE, d 'de' MMMM, yyyy", Locale("es", "ES"))
    val fecha = Date(timestamp * 1000)
    return formato.format(fecha).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}






