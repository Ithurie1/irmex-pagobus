package com.irmex.estudiante.presentation.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.domain.model.Perfil
import com.irmex.estudiante.presentation.common.MensajesEffect

/** Módulo "Mi Perfil": datos personales y domicilio del administrador en sesión. */
@Composable
fun PerfilScreen(snackbar: SnackbarHostState, onCerrarSesion: () -> Unit, viewModel: PerfilViewModel = hiltViewModel()) {
    val perfil by viewModel.perfil.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    val datos = perfil
    if (datos == null) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text(
                if (cargando) "Cargando perfil…" else "No se pudo cargar tu perfil. Revisa tu conexión y vuelve a entrar al módulo.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        FormularioPerfil(datos, cargando, viewModel::guardar, onCerrarSesion)
    }
}

@Composable
private fun FormularioPerfil(
    perfil: Perfil,
    guardando: Boolean,
    onGuardar: (nombre: String, curp: String, calle: String, numero: String, colonia: String, cp: String) -> Unit,
    onCerrarSesion: () -> Unit,
) {
    // El texto de los campos vive aquí (estado de UI); al guardar se entrega al ViewModel.
    var nombre by remember(perfil) { mutableStateOf(perfil.nombre) }
    var curp by remember(perfil) { mutableStateOf(perfil.curp) }
    var calle by remember(perfil) { mutableStateOf(perfil.calle) }
    var numero by remember(perfil) { mutableStateOf(perfil.numero) }
    var colonia by remember(perfil) { mutableStateOf(perfil.colonia) }
    var cp by remember(perfil) { mutableStateOf(perfil.cp) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Datos personales", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = perfil.correo, onValueChange = {}, label = { Text("Correo") },
            enabled = false, singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = perfil.fechaRegistro, onValueChange = {}, label = { Text("Miembro desde") },
            enabled = false, singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = curp, onValueChange = { curp = it.take(18) }, label = { Text("CURP (18 caracteres)") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(4.dp))
        Text("Domicilio", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = calle, onValueChange = { calle = it }, label = { Text("Calle") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = numero, onValueChange = { numero = it }, label = { Text("Número") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = colonia, onValueChange = { colonia = it }, label = { Text("Colonia") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = cp, onValueChange = { cp = it.filter(Char::isDigit).take(5) }, label = { Text("Código postal") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = { onGuardar(nombre, curp, calle, numero, colonia, cp) },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) { Text("Guardar cambios") }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Cerrar sesión") }
    }
}
