package com.irmex.admin.presentation.tickets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Ticket
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.theme.Ambar
import com.irmex.admin.presentation.theme.Verde

@Composable
fun TicketsScreen(snackbar: SnackbarHostState, viewModel: TicketsViewModel = hiltViewModel()) {
    val tickets by viewModel.tickets.collectAsStateWithLifecycle()
    val carpeta by viewModel.carpeta.collectAsStateWithLifecycle()
    val sinLeer by viewModel.sinLeer.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val enRespuesta by viewModel.enRespuesta.collectAsStateWithLifecycle()
    val porBorrar by viewModel.porBorrar.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    Column {
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Carpeta.entries.forEach { opcion ->
                FilterChip(
                    selected = opcion == carpeta,
                    onClick = { viewModel.elegirCarpeta(opcion) },
                    label = {
                        val conteo = if (opcion == Carpeta.BANDEJA && sinLeer > 0) " ($sinLeer)" else ""
                        Text(opcion.titulo + conteo)
                    },
                )
            }
        }
        ListaRefrescable(
            items = tickets,
            cargando = cargando,
            textoVacio = "${carpeta.textoVacio}\nDesliza hacia abajo para actualizar.",
            onRefrescar = viewModel::refrescar,
            clave = { it.id },
        ) { ticket ->
            TicketItem(
                ticket = ticket,
                onLeido = { viewModel.alternarLeido(ticket) },
                onImportante = { viewModel.alternarImportante(ticket) },
                onResponder = { viewModel.abrirRespuesta(ticket) },
                onBorrar = { viewModel.pedirBorrado(ticket) },
            )
        }
    }

    enRespuesta?.let { ticket ->
        DialogoRespuesta(
            ticket = ticket,
            onEnviar = viewModel::enviarRespuesta,
            onCerrar = viewModel::cerrarRespuesta,
        )
    }

    porBorrar?.let { ticket ->
        AlertDialog(
            onDismissRequest = viewModel::cancelarBorrado,
            title = { Text("Eliminar ticket") },
            text = { Text("¿Eliminar \"${ticket.asunto}\" de forma permanente? Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = viewModel::confirmarBorrado) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = viewModel::cancelarBorrado) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun TicketItem(
    ticket: Ticket,
    onLeido: () -> Unit,
    onImportante: () -> Unit,
    onResponder: () -> Unit,
    onBorrar: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Column {
                Text(ticket.asunto, style = MaterialTheme.typography.titleMedium)
                Text("${ticket.remitente} · ${ticket.correo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(ticket.mensaje)
            if (!ticket.respuesta.isNullOrBlank()) {
                Text("Respuesta: ${ticket.respuesta}", color = Verde, style = MaterialTheme.typography.bodyMedium)
            }
            Text(ticket.fecha.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    IconButton(onClick = onLeido) {
                        Icon(
                            imageVector = if (ticket.revisado) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                            contentDescription = if (ticket.revisado) "Marcar como no leído" else "Marcar como leído",
                            tint = if (ticket.revisado) Verde else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onImportante) {
                        Icon(
                            imageVector = if (ticket.importante) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (ticket.importante) "Quitar de destacados" else "Destacar",
                            tint = Ambar,
                        )
                    }
                    IconButton(onClick = onBorrar) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar ticket", tint = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onResponder) {
                    Text(if (ticket.respuesta.isNullOrBlank()) "Responder" else "Editar respuesta")
                }
            }
        }
    }
}

@Composable
private fun DialogoRespuesta(ticket: Ticket, onEnviar: (String) -> Unit, onCerrar: () -> Unit) {
    var texto by remember(ticket.id) { mutableStateOf(ticket.respuesta.orEmpty()) }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Responder a ${ticket.remitente}") },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Respuesta") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = { TextButton(onClick = { onEnviar(texto) }) { Text("Enviar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } },
    )
}
