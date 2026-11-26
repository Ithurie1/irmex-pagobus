package com.irmex.estudiante.presentation.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/** Confirmación antes de cerrar sesión, para evitar salir por un toque accidental. */
@Composable
fun DialogoCerrarSesion(onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cerrar sesión") },
        text = { Text("¿Seguro que quieres salir? Tu información guardada en este teléfono se borrará.") },
        confirmButton = { TextButton(onClick = onConfirmar) { Text("Cerrar sesión") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}
