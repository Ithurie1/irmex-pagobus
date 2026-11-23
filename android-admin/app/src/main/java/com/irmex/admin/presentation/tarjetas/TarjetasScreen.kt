package com.irmex.admin.presentation.tarjetas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.presentation.common.EstatusChip
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.common.formatoMoneda

@Composable
fun TarjetasScreen(snackbar: SnackbarHostState, viewModel: TarjetasViewModel = hiltViewModel()) {
    val tarjetas by viewModel.tarjetas.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    ListaRefrescable(
        items = tarjetas,
        cargando = cargando,
        textoVacio = "No hay tarjetas para mostrar.\nDesliza hacia abajo para actualizar.",
        onRefrescar = viewModel::refrescar,
        clave = { it.id },
    ) { TarjetaItem(it) }
}

@Composable
private fun TarjetaItem(tarjeta: Tarjeta) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tarjeta.numero, style = MaterialTheme.typography.titleMedium)
                EstatusChip(tarjeta.estatus)
            }
            Text(tarjeta.propietario, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatoMoneda(tarjeta.saldo), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                Text("Vigencia: ${tarjeta.vigencia}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
