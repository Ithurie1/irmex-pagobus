package com.irmex.admin.presentation.renovaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect

@Composable
fun RenovacionesScreen(snackbar: SnackbarHostState, viewModel: RenovacionesViewModel = hiltViewModel()) {
    val renovaciones by viewModel.renovaciones.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    ListaRefrescable(
        items = renovaciones,
        cargando = cargando,
        textoVacio = "No hay renovaciones pendientes.\nDesliza hacia abajo para actualizar.",
        onRefrescar = viewModel::refrescar,
        clave = { it.id },
    ) { renovacion ->
        RenovacionItem(
            renovacion = renovacion,
            onAprobar = { viewModel.aprobar(renovacion) },
            onRechazar = { viewModel.rechazar(renovacion) },
        )
    }
}

@Composable
private fun RenovacionItem(renovacion: Renovacion, onAprobar: () -> Unit, onRechazar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(renovacion.nombreUsuario, style = MaterialTheme.typography.titleMedium)
            Text(renovacion.correo, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Documento: ${renovacion.nombreArchivo}", style = MaterialTheme.typography.bodySmall)
            Text("Solicitada: ${renovacion.fecha.take(10)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onAprobar, modifier = Modifier.weight(1f)) { Text("Aprobar") }
                OutlinedButton(onClick = onRechazar, modifier = Modifier.weight(1f)) { Text("Rechazar") }
            }
        }
    }
}
