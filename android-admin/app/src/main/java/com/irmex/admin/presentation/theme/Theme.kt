package com.irmex.admin.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Identidad visual de IrMex Admin: gris pizarra / azul marino con acentos ámbar.
val Pizarra = Color(0xFF1C232E)
val PizarraClara = Color(0xFF263041)
val AzulMarino = Color(0xFF0F2A4A)
val Ambar = Color(0xFFFFB300)
val AmbarOscuro = Color(0xFF5C4300)
val TextoClaro = Color(0xFFE8EDF4)
val TextoApagado = Color(0xFF9AA7B8)
val Rojo = Color(0xFFFF6B6B)
val Verde = Color(0xFF4CD08B)

private val EsquemaOscuro = darkColorScheme(
    primary = Ambar,
    onPrimary = Color(0xFF1B1300),
    primaryContainer = AmbarOscuro,
    onPrimaryContainer = Color(0xFFFFE08A),
    secondary = Color(0xFF7FA6D9),
    onSecondary = Color(0xFF00203F),
    background = Pizarra,
    onBackground = TextoClaro,
    surface = Pizarra,
    onSurface = TextoClaro,
    surfaceVariant = PizarraClara,
    onSurfaceVariant = TextoApagado,
    surfaceContainer = PizarraClara,
    surfaceContainerHigh = AzulMarino,
    error = Rojo,
    onError = Color(0xFF2B0000),
    outline = Color(0xFF3B4759),
)

@Composable
fun IrmexAdminTheme(content: @Composable () -> Unit) {
    // La app es siempre oscura para diferenciarla de la app de estudiantes.
    MaterialTheme(colorScheme = EsquemaOscuro, content = content)
}
