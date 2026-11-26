package com.irmex.estudiante.presentation.soporte

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.domain.model.Reporte
import com.irmex.estudiante.presentation.common.ListaRefrescable
import com.irmex.estudiante.presentation.common.MensajesEffect
import com.irmex.estudiante.presentation.theme.Ambar
import com.irmex.estudiante.presentation.theme.Menta

private val ASUNTOS = listOf(
    "Recarga no reflejada",
    "Tarjeta bloqueada",
    "Duda sobre renovación",
    "Otro",
)

private val TIPOS_EVIDENCIA = arrayOf("application/pdf", "image/jpeg", "image/png")

/** "Mis reportes": historial de los reportes que el estudiante envió y botón para crear uno nuevo. */
@Composable
fun SoporteScreen(snackbar: SnackbarHostState, viewModel: SoporteViewModel = hiltViewModel()) {
    val reportes by viewModel.reportes.collectAsStateWithLifecycle()
    val formulario by viewModel.formulario.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    Scaffold(
        // El marco de la app ya respeta las barras del sistema; aquí no se duplica el margen.
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::abrirFormulario,
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                text = { Text("Nuevo reporte") },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                Text("Mis reportes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Revisa el estado de tus quejas y dudas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            ListaRefrescable(
                items = reportes,
                cargando = cargando,
                textoVacio = "No tienes reportes activos.\nSi necesitas ayuda, pulsa \"Nuevo reporte\".",
                onRefrescar = viewModel::refrescar,
                clave = { it.id },
            ) { ReporteItem(it) }
        }
    }

    if (formulario.abierto) {
        DialogoNuevoReporte(
            formulario = formulario,
            onEnviar = viewModel::enviar,
            onCerrar = viewModel::cerrarFormulario,
            onElegirEvidencia = viewModel::elegirEvidencia,
            onQuitarEvidencia = viewModel::quitarEvidencia,
        )
    }
}

@Composable
private fun ReporteItem(reporte: Reporte) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(reporte.asunto, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text(
                    if (reporte.resuelto) "Resuelto" else "En revisión",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (reporte.resuelto) Menta else Ambar,
                )
            }
            Text(reporte.mensaje, style = MaterialTheme.typography.bodyMedium)
            Text(reporte.fecha.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!reporte.respuesta.isNullOrBlank()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Respuesta de administración", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text(reporte.respuesta, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoNuevoReporte(
    formulario: FormularioReporte,
    onEnviar: (asunto: String, mensaje: String) -> Unit,
    onCerrar: () -> Unit,
    onElegirEvidencia: (String) -> Unit,
    onQuitarEvidencia: () -> Unit,
) {
    var asunto by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf("") }
    var menuAbierto by rememberSaveable { mutableStateOf(false) }

    val selector = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onElegirEvidencia(uri.toString())
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo reporte") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box {
                    OutlinedButton(onClick = { menuAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(asunto.ifEmpty { "Selecciona el problema…" })
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        ASUNTOS.forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = { asunto = opcion; menuAbierto = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = mensaje, onValueChange = { mensaje = it }, label = { Text("Cuéntanos qué pasó") },
                    minLines = 3, modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(onClick = { selector.launch(TIPOS_EVIDENCIA) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.AttachFile, contentDescription = null)
                    Text(formulario.evidenciaNombre ?: "Adjuntar evidencia (opcional)", modifier = Modifier.padding(start = 8.dp))
                }
                if (formulario.evidenciaUri != null) {
                    TextButton(onClick = onQuitarEvidencia) { Text("Quitar archivo") }
                }
                formulario.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEnviar(asunto, mensaje) }, enabled = !formulario.enviando) {
                Text(if (formulario.enviando) "Enviando…" else "Enviar")
            }
        },
        dismissButton = { TextButton(onClick = onCerrar, enabled = !formulario.enviando) { Text("Cancelar") } },
    )
}
