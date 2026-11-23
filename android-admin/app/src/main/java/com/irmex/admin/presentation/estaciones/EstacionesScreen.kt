package com.irmex.admin.presentation.estaciones

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
import com.irmex.admin.domain.model.Estacion
import com.irmex.admin.presentation.common.EstatusChip
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect

@Composable
fun EstacionesScreen(snackbar: SnackbarHostState, viewModel: EstacionesViewModel = hiltViewModel()) {
    val estaciones by viewModel.estaciones.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    ListaRefrescable(
        items = estaciones,
        cargando = cargando,
        textoVacio = "No hay estaciones para mostrar.\nDesliza hacia abajo para actualizar.",
        onRefrescar = viewModel::refrescar,
        clave = { it.id },
    ) { EstacionItem(it) }
}

@Composable
private fun EstacionItem(estacion: Estacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(estacion.nombre, style = MaterialTheme.typography.titleMedium)
                Text(estacion.tipo, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            EstatusChip(estacion.estatus)
        }
    }
}
