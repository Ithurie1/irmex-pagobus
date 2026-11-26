package com.irmex.estudiante.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.presentation.common.formatoFecha
import com.irmex.estudiante.presentation.theme.DegradadoBilletera

private enum class PantallaAuth { LOGIN, REGISTRO, RECUPERAR }

/** Flujo de acceso: inicio de sesión, registro y recuperación de contraseña. */
@Composable
fun AuthHost() {
    var pantalla by rememberSaveable { mutableStateOf(PantallaAuth.LOGIN) }
    when (pantalla) {
        PantallaAuth.LOGIN -> LoginScreen(
            onRegistro = { pantalla = PantallaAuth.REGISTRO },
            onRecuperar = { pantalla = PantallaAuth.RECUPERAR },
        )
        PantallaAuth.REGISTRO -> RegistroScreen(onVolver = { pantalla = PantallaAuth.LOGIN })
        PantallaAuth.RECUPERAR -> RecuperarScreen(onVolver = { pantalla = PantallaAuth.LOGIN })
    }
}

/** Marco común: franja con degradado y logo arriba, y una hoja blanca redondeada con el formulario. */
@Composable
private fun MarcoBienvenida(titulo: String, subtitulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(DegradadoBilletera)) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 56.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text("IrMex", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Tu pase de transporte, en tu bolsillo", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.85f))
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().fillMaxHeight(0.78f),
            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                contenido()
            }
        }
    }
}

@Composable
private fun BotonPrincipal(texto: String, cargando: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = !cargando, modifier = Modifier.fillMaxWidth().height(54.dp)) {
        if (cargando) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) else Text(texto)
    }
}

@Composable
private fun DialogoAuth(estado: AuthUiState, onCerrar: () -> Unit) {
    estado.mensaje?.let { mensaje ->
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text(estado.titulo) },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = onCerrar) { Text("Entendido") } },
        )
    }
}

@Composable
private fun LoginScreen(onRegistro: () -> Unit, onRecuperar: () -> Unit, viewModel: LoginViewModel = hiltViewModel()) {
    val estado by viewModel.state.collectAsStateWithLifecycle()
    // El texto de los campos vive en la pantalla (estado de UI puro) y se entrega al ViewModel al ingresar.
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    MarcoBienvenida("Hola de nuevo", "Inicia sesión para ver tu saldo y tus recargas.") {
        OutlinedTextField(
            value = correo, onValueChange = { correo = it }, label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Contraseña") },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(onClick = onRecuperar, modifier = Modifier.align(Alignment.End)) { Text("¿Olvidaste tu contraseña?") }
        BotonPrincipal("Ingresar", estado.cargando) { viewModel.iniciarSesion(correo, password) }
        if (estado.cargando) {
            Text(
                "Conectando… la primera vez puede tardar hasta un minuto.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        TextButton(onClick = onRegistro, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Eres nuevo? Crea tu cuenta")
        }
    }
    DialogoAuth(estado, viewModel::cerrarMensaje)
}

@Composable
private fun RegistroScreen(onVolver: () -> Unit, viewModel: RegistroViewModel = hiltViewModel()) {
    val estado by viewModel.state.collectAsStateWithLifecycle()
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }

    MarcoBienvenida("Crea tu cuenta", "Te asignamos tu tarjeta IrMex; la activas subiendo tu comprobante de estudios.") {
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre completo") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = correo, onValueChange = { correo = it }, label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Contraseña (mínimo 6 caracteres)") },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = fecha, onValueChange = { fecha = formatoFecha(it) }, label = { Text("Fecha de nacimiento (AAAA-MM-DD)") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        BotonPrincipal("Crear cuenta", estado.cargando) { viewModel.crearCuenta(nombre, correo, password, fecha) }
        TextButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Ya tengo cuenta")
        }
    }
    // Si la cuenta se creó pero el acceso automático falló, el diálogo lleva de vuelta al login.
    DialogoAuth(estado) {
        viewModel.cerrarMensaje()
        if (estado.exito) onVolver()
    }
}

@Composable
private fun RecuperarScreen(onVolver: () -> Unit, viewModel: RecuperarViewModel = hiltViewModel()) {
    val estado by viewModel.state.collectAsStateWithLifecycle()
    var correo by rememberSaveable { mutableStateOf("") }

    MarcoBienvenida("Recupera tu contraseña", "Escribe tu correo y te enviamos un enlace para crear una nueva.") {
        OutlinedTextField(
            value = correo, onValueChange = { correo = it }, label = { Text("Correo electrónico") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        BotonPrincipal("Enviar enlace", estado.cargando) { viewModel.enviarEnlace(correo) }
        TextButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver a iniciar sesión")
        }
    }
    DialogoAuth(estado) {
        viewModel.cerrarMensaje()
        if (estado.exito) onVolver()
    }
}
