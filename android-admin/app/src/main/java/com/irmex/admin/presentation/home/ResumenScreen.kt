package com.irmex.admin.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.common.formatoMoneda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(snackbar: SnackbarHostState, viewModel: ResumenViewModel = hiltViewModel()) {
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    PullToRefreshBox(isRefreshing = cargando, onRefresh = viewModel::refrescar, modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val r = resumen
            if (r == null) {
                item {
                    Text(
                        if (cargando) "Cargando resumen…" else "Desliza hacia abajo para cargar el resumen.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                item { Dato("Estudiantes registrados", r.totalEstudiantes.toString()) }
                item { Dato("Tarjetas activas", r.tarjetasActivas.toString()) }
                item { Dato("Tarjetas inactivas", r.tarjetasInactivas.toString()) }
                item { Dato("Ingresos por recargas", formatoMoneda(r.totalIngresos)) }
                item { Dato("Gastos en pasajes", formatoMoneda(r.totalGastos)) }
            }
        }
    }
}

@Composable
private fun Dato(titulo: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
