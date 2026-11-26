package com.irmex.admin.presentation.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.AccesoLog
import com.irmex.admin.domain.model.ResumenGeneral
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.common.etiquetaRol
import com.irmex.admin.presentation.common.formatoMoneda
import com.irmex.admin.presentation.theme.Rojo
import com.irmex.admin.presentation.theme.Verde

/** Módulo "Reportes de Uso": métricas, flujo financiero y auditoría de los últimos accesos. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(snackbar: SnackbarHostState, viewModel: ReportesViewModel = hiltViewModel()) {
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()
    val accesos by viewModel.accesos.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    PullToRefreshBox(isRefreshing = cargando, onRefresh = viewModel::refrescar, modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val datos = resumen
            if (datos == null) {
                item {
                    Text(
                        if (cargando) "Cargando reportes…" else "Desliza hacia abajo para cargar los reportes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                item { Metricas(datos) }
                item { GraficaFinanciera(datos.totalIngresos, datos.totalGastos) }
            }

            item {
                Text(
                    "Auditoría de accesos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    "Últimos 50 inicios de sesión exitosos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (accesos.isEmpty()) {
                item {
                    Text(
                        if (cargando) "Cargando accesos…" else "Sin accesos registrados.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(accesos) { AccesoItem(it) }
            }
        }
    }
}

@Composable
private fun Metricas(r: ResumenGeneral) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Metrica("Estudiantes", r.totalEstudiantes.toString(), Modifier.weight(1f))
            Metrica("Tarjetas activas", r.tarjetasActivas.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Metrica("Tarjetas inactivas", r.tarjetasInactivas.toString(), Modifier.weight(1f))
            Metrica("Ingresos totales", formatoMoneda(r.totalIngresos), Modifier.weight(1f), Verde)
        }
        Metrica("Pasajes cobrados", formatoMoneda(r.totalGastos), Modifier.fillMaxWidth(), Rojo)
    }
}

@Composable
private fun Metrica(
    titulo: String,
    valor: String,
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.headlineSmall, color = color)
        }
    }
}

/** Gráfica de barras de ingresos contra gastos, dibujada solo con Compose (sin librerías). */
@Composable
private fun GraficaFinanciera(ingresos: Double, gastos: Double) {
    val maximo = maxOf(ingresos, gastos, 1.0)
    val alturaMaxima = 140.dp
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Flujo financiero", style = MaterialTheme.typography.titleMedium)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                Barra("Ingresos (recargas)", ingresos, maximo, alturaMaxima, Verde)
                Barra("Gastos (pasajes)", gastos, maximo, alturaMaxima, Rojo)
            }
        }
    }
}

@Composable
private fun Barra(etiqueta: String, valor: Double, maximo: Double, alturaMaxima: Dp, color: Color) {
    // Altura mínima visible para que una barra en cero se note como tal.
    val altura = maxOf(alturaMaxima * (valor / maximo).toFloat(), 4.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(formatoMoneda(valor), style = MaterialTheme.typography.labelLarge, color = color)
        Box(
            Modifier
                .width(56.dp)
                .height(altura)
                .background(color, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
        )
        Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AccesoItem(acceso: AccesoLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(acceso.correo, style = MaterialTheme.typography.bodyMedium)
                Text(acceso.fecha, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(etiquetaRol(acceso.rol), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
