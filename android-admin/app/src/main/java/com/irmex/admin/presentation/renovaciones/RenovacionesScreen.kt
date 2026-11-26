package com.irmex.admin.presentation.renovaciones

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect

@Composable
fun RenovacionesScreen(snackbar: SnackbarHostState, viewModel: RenovacionesViewModel = hiltViewModel()) {
    val renovaciones by viewModel.renovaciones.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val documento by viewModel.documento.collectAsStateWithLifecycle()
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
            onVerDocumento = { viewModel.verDocumento(renovacion) },
            onAprobar = { viewModel.aprobar(renovacion) },
            onRechazar = { viewModel.rechazar(renovacion) },
        )
    }

    documento?.let { VisorDocumento(estado = it, onCerrar = viewModel::cerrarDocumento) }
}

@Composable
private fun RenovacionItem(
    renovacion: Renovacion,
    onVerDocumento: () -> Unit,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(renovacion.nombreUsuario, style = MaterialTheme.typography.titleMedium)
            Text(renovacion.correo, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Solicitada: ${renovacion.fecha.take(10)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = onVerDocumento, modifier = Modifier.fillMaxWidth()) {
                Text("Ver documento (${renovacion.nombreArchivo})")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onAprobar, modifier = Modifier.weight(1f)) { Text("Aprobar") }
                OutlinedButton(onClick = onRechazar, modifier = Modifier.weight(1f)) { Text("Rechazar") }
            }
        }
    }
}

/** Visor a pantalla completa: desplazamiento vertical/horizontal y botones para acercar o alejar. */
@Composable
private fun VisorDocumento(estado: DocumentoUi, onCerrar: () -> Unit) {
    Dialog(onDismissRequest = onCerrar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            var escala by remember { mutableFloatStateOf(1f) }
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        estado.titulo,
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                    )
                    TextButton(onClick = { escala = (escala - 0.5f).coerceAtLeast(1f) }) { Text("−") }
                    TextButton(onClick = { escala = (escala + 0.5f).coerceAtMost(4f) }) { Text("+") }
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar documento")
                    }
                }
                when (estado) {
                    is DocumentoUi.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is DocumentoUi.Listo -> {
                        val anchoPantalla = LocalConfiguration.current.screenWidthDp.dp
                        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            Row(Modifier.horizontalScroll(rememberScrollState())) {
                                Column {
                                    estado.paginas.forEach { pagina ->
                                        Image(
                                            bitmap = pagina,
                                            contentDescription = "Documento del estudiante",
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier.width(anchoPantalla * escala),
                                        )
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
