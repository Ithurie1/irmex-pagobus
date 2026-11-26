package com.irmex.admin.presentation.tarjetas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.domain.model.Usuario
import com.irmex.admin.presentation.common.EstatusChip
import com.irmex.admin.presentation.common.ListaRefrescable
import com.irmex.admin.presentation.common.MensajesEffect
import com.irmex.admin.presentation.common.formatoMoneda

@Composable
fun TarjetasScreen(snackbar: SnackbarHostState, viewModel: TarjetasViewModel = hiltViewModel()) {
    val tarjetas by viewModel.tarjetas.collectAsStateWithLifecycle()
    val estudiantes by viewModel.estudiantes.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val dialogo by viewModel.dialogo.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    Scaffold(
        // El marco del módulo ya respeta las barras del sistema; aquí no se duplica el margen.
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.abrir(DialogoTarjeta.Alta) }) {
                Icon(Icons.Filled.Add, contentDescription = "Registrar tarjeta")
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            ListaRefrescable(
                items = tarjetas,
                cargando = cargando,
                textoVacio = "No hay tarjetas para mostrar.\nDesliza hacia abajo para actualizar.",
                onRefrescar = viewModel::refrescar,
                clave = { it.id },
            ) { tarjeta ->
                TarjetaItem(
                    tarjeta = tarjeta,
                    onEstatus = { viewModel.abrir(DialogoTarjeta.Estatus(tarjeta)) },
                    onVigencia = { viewModel.abrir(DialogoTarjeta.Vigencia(tarjeta)) },
                    onRecarga = { viewModel.abrir(DialogoTarjeta.Recarga(tarjeta)) },
                    onBorrar = { viewModel.abrir(DialogoTarjeta.Borrar(tarjeta)) },
                )
            }
        }
    }

    when (val actual = dialogo) {
        null -> Unit
        DialogoTarjeta.Alta -> DialogoAlta(estudiantes, viewModel::confirmarAlta, viewModel::cerrar)
        is DialogoTarjeta.Vigencia -> DialogoVigencia(actual.tarjeta, viewModel::confirmarVigencia, viewModel::cerrar)
        is DialogoTarjeta.Recarga -> DialogoRecarga(actual.tarjeta, viewModel::confirmarRecarga, viewModel::cerrar)
        is DialogoTarjeta.Estatus -> DialogoNip(
            titulo = if (actual.tarjeta.estatus.lowercase() == "activa") "Bloquear tarjeta" else "Activar tarjeta",
            detalle = "Tarjeta ${actual.tarjeta.numero}. Escribe el NIP maestro para autorizar.",
            onConfirmar = { viewModel.confirmarEstatus(actual.tarjeta, it) },
            onCancelar = viewModel::cerrar,
        )
        is DialogoTarjeta.Borrar -> DialogoNip(
            titulo = "Borrar tarjeta",
            detalle = "Se eliminará la tarjeta ${actual.tarjeta.numero} y su historial de forma permanente. " +
                "Escribe el NIP maestro para autorizar.",
            onConfirmar = { viewModel.confirmarBorrado(actual.tarjeta, it) },
            onCancelar = viewModel::cerrar,
        )
    }
}

@Composable
private fun TarjetaItem(
    tarjeta: Tarjeta,
    onEstatus: () -> Unit,
    onVigencia: () -> Unit,
    onRecarga: () -> Unit,
    onBorrar: () -> Unit,
) {
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onRecarga) { Text("Recarga") }
                TextButton(onClick = onVigencia) { Text("Vigencia") }
                TextButton(onClick = onEstatus) {
                    Text(if (tarjeta.estatus.lowercase() == "activa") "Bloquear" else "Activar")
                }
                TextButton(onClick = onBorrar) { Text("Borrar", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun DialogoAlta(
    estudiantes: List<Usuario>,
    onConfirmar: (numero: String, saldo: String, estatus: String, usuarioId: Int?) -> Unit,
    onCancelar: () -> Unit,
) {
    var numero by remember { mutableStateOf("") }
    var saldo by remember { mutableStateOf("0") }
    var activa by remember { mutableStateOf(true) }
    var estudiante by remember { mutableStateOf<Usuario?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar tarjeta") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = numero,
                    onValueChange = { numero = it.filter(Char::isDigit).take(16) },
                    label = { Text("Número de tarjeta (16 dígitos)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = saldo,
                    onValueChange = { saldo = it.filter(Char::isDigit) },
                    label = { Text("Saldo inicial ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Box {
                    OutlinedButton(onClick = { menuAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(estudiante?.let { "${it.nombre} (${it.correo})" } ?: "Sin asignar")
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        DropdownMenuItem(text = { Text("Sin asignar") }, onClick = { estudiante = null; menuAbierto = false })
                        estudiantes.forEach { e ->
                            DropdownMenuItem(
                                text = { Text("${e.nombre} (${e.correo})") },
                                onClick = { estudiante = e; menuAbierto = false },
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { activa = !activa }) { Text(if (activa) "Estatus: Activa" else "Estatus: Inactiva") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(numero, saldo, if (activa) "Activa" else "Inactiva", estudiante?.id) }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}

@Composable
private fun DialogoVigencia(tarjeta: Tarjeta, onConfirmar: (Tarjeta, String) -> Unit, onCancelar: () -> Unit) {
    var dias by remember { mutableStateOf("30") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Ajustar vigencia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Vigencia actual: ${tarjeta.vigencia}. Escribe los días a sumar (negativo para restar).")
                OutlinedTextField(
                    value = dias,
                    onValueChange = { dias = it },
                    label = { Text("Días") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirmar(tarjeta, dias) }) { Text("Aplicar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}

@Composable
private fun DialogoRecarga(tarjeta: Tarjeta, onConfirmar: (Tarjeta, String, String) -> Unit, onCancelar: () -> Unit) {
    var monto by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Recarga manual") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tarjeta ${tarjeta.numero}. Acción sensible: requiere el NIP maestro.")
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto a recargar ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = { Text("NIP maestro") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirmar(tarjeta, monto, nip) }) { Text("Autorizar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}

@Composable
private fun DialogoNip(titulo: String, detalle: String, onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    var nip by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(detalle)
                OutlinedTextField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = { Text("NIP maestro") },
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
