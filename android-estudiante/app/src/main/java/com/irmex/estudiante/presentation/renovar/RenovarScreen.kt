package com.irmex.estudiante.presentation.renovar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.presentation.common.MensajesEffect

private val TIPOS_DOCUMENTO = arrayOf("application/pdf", "image/jpeg", "image/png")

/** Renovar tarjeta: el estudiante sube su comprobante de estudios o INE para que un administrador lo revise. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenovarScreen(snackbar: SnackbarHostState, onVolver: () -> Unit, viewModel: RenovarViewModel = hiltViewModel()) {
    val estado by viewModel.state.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    val selector = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.elegirArchivo(uri.toString())
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Renovar tarjeta") },
            navigationIcon = {
                IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
            },
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Filled.UploadFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(72.dp))
            Text("Renueva tu tarifa preferencial", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Sube tu comprobante de estudios o INE en formato PDF o imagen (JPG o PNG, máximo 10 MB). " +
                    "Un administrador lo revisará y, si todo está bien, activará tu tarjeta.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(onClick = { selector.launch(TIPOS_DOCUMENTO) }, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                Text(estado.archivoNombre ?: "Seleccionar archivo")
            }
            Button(
                onClick = viewModel::enviar,
                enabled = estado.archivoUri != null && !cargando,
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) {
                if (cargando) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) else Text("Enviar solicitud")
            }
        }
    }

    if (estado.enviado) {
        AlertDialog(
            onDismissRequest = onVolver,
            title = { Text("Solicitud enviada") },
            text = { Text("Tu documento quedó en revisión. Te llegará la activación de tu tarjeta cuando un administrador lo apruebe.") },
            confirmButton = { TextButton(onClick = onVolver) { Text("Entendido") } },
        )
    }
}
