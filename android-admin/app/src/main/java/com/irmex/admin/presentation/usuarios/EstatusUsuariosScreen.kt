package com.irmex.admin.presentation.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Usuario
import com.irmex.admin.presentation.common.EstatusChip
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.common.etiquetaRol

/** Módulo "Verificar Estatus de Usuario": directorio con búsqueda y baja de usuarios. */
@Composable
fun EstatusUsuariosScreen(snackbar: SnackbarHostState, viewModel: UsuariosViewModel = hiltViewModel()) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val consulta by viewModel.consulta.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val porBaja by viewModel.usuarioPorBaja.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = consulta,
            onValueChange = viewModel::onConsultaChange,
            label = { Text("Buscar por nombre o correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        ListaRefrescable(
            items = usuarios,
            cargando = cargando,
            textoVacio = "No hay usuarios para mostrar.\nDesliza hacia abajo para actualizar.",
            onRefrescar = viewModel::refrescar,
            clave = { it.id },
        ) { usuario -> UsuarioItem(usuario, onBaja = { viewModel.pedirBaja(usuario) }) }
    }

    porBaja?.let { usuario ->
        AlertDialog(
            onDismissRequest = viewModel::cancelarBaja,
            title = { Text("Dar de baja") },
            text = { Text("¿Dar de baja a ${usuario.nombre}? Su cuenta y sus tarjetas quedarán inactivas.") },
            confirmButton = { TextButton(onClick = viewModel::confirmarBaja) { Text("Dar de baja") } },
            dismissButton = { TextButton(onClick = viewModel::cancelarBaja) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun UsuarioItem(usuario: Usuario, onBaja: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                EstatusChip(usuario.estatusTarjeta)
            }
            Text(usuario.correo, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Acceso: ${etiquetaRol(usuario.rol)}", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onBaja) {
                    Text("Dar de baja", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
