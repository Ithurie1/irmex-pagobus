package com.irmex.estudiante.presentation.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.domain.model.Movimiento
import com.irmex.estudiante.presentation.common.ListaRefrescable
import com.irmex.estudiante.presentation.common.MensajesEffect
import com.irmex.estudiante.presentation.common.formatoMoneda
import com.irmex.estudiante.presentation.theme.Coral
import com.irmex.estudiante.presentation.theme.Menta

@Composable
fun HistorialScreen(snackbar: SnackbarHostState, viewModel: HistorialViewModel = hiltViewModel()) {
    val movimientos by viewModel.movimientos.collectAsStateWithLifecycle()
    val tarjeta by viewModel.tarjeta.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
            Text("Historial de uso", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                tarjeta?.let { "Tarjeta terminación •••• ${it.ultimosDigitos}" } ?: "Tus recargas y viajes",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        ListaRefrescable(
            items = movimientos,
            cargando = cargando,
            textoVacio = "No tienes movimientos recientes.\nDesliza hacia abajo para actualizar.",
            onRefrescar = viewModel::refrescar,
            clave = { it.id },
        ) { MovimientoItem(it) }
    }
}

@Composable
private fun MovimientoItem(movimiento: Movimiento) {
    val color = if (movimiento.esRecarga) Menta else Coral
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (movimiento.esRecarga) Icons.Filled.AddCircle else Icons.Filled.DirectionsBus,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.12f), CircleShape).padding(8.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(movimiento.tipo, style = MaterialTheme.typography.titleSmall)
                if (!movimiento.esRecarga) {
                    Text("${movimiento.origen} → ${movimiento.destino}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(movimiento.fecha, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                (if (movimiento.esRecarga) "+" else "-") + formatoMoneda(movimiento.monto),
                color = color,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
