package com.irmex.admin.presentation.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Usuario
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.common.ROLES_ASIGNABLES
import com.irmex.admin.presentation.common.etiquetaRol

/** Módulo "Gestor de Privilegios" (solo superadmin): cambia el rol de un usuario con NIP. */
@Composable
fun PrivilegiosScreen(snackbar: SnackbarHostState, viewModel: UsuariosViewModel = hiltViewModel()) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val consulta by viewModel.consulta.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val pendiente by viewModel.cambioPendiente.collectAsStateWithLifecycle()
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
        ) { usuario ->
            PrivilegioItem(usuario, onElegirRol = { viewModel.pedirCambioRol(usuario, it) })
        }
    }

    pendiente?.let { cambio ->
        DialogoNip(
            cambio = cambio,
            onConfirmar = viewModel::confirmarCambioRol,
            onCancelar = viewModel::cancelarCambioRol,
        )
    }
}

@Composable
private fun PrivilegioItem(usuario: Usuario, onElegirRol: (String) -> Unit) {
    var menuAbierto by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                Text(usuario.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box {
                OutlinedButton(onClick = { menuAbierto = true }) { Text(etiquetaRol(usuario.rol)) }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    ROLES_ASIGNABLES.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(etiquetaRol(rol)) },
                            onClick = {
                                menuAbierto = false
                                onElegirRol(rol)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoNip(cambio: CambioPendiente, onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    var nip by remember(cambio) { mutableStateOf("") }
    val esSuperadmin = cambio.usuario.rol.lowercase() == "superadmin" || cambio.nuevoRol == "superadmin"
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (esSuperadmin) "Autorización de máxima seguridad" else "Autorizar cambio de rol") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Cambiar a ${cambio.usuario.nombre} de ${etiquetaRol(cambio.usuario.rol)} " +
                        "a ${etiquetaRol(cambio.nuevoRol)}. " +
                        if (esSuperadmin) "Escribe el NIP raíz." else "Escribe el NIP maestro.",
                )
                OutlinedTextField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = { Text("NIP") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirmar(nip) }) { Text("Autorizar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}
