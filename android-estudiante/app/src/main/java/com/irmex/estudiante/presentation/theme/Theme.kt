package com.irmex.estudiante.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Identidad visual de IrMex Estudiante: clara, morada y turquesa, con billetera en degradado.
// (La app de administradores es oscura y ámbar: aquí todo es luminoso y redondeado.)
val Violeta = Color(0xFF5B3CC4)
val VioletaProfundo = Color(0xFF3A1F99)
val VioletaClaro = Color(0xFFEAE4FF)
val Turquesa = Color(0xFF00B8A9)
val TurquesaClaro = Color(0xFFD6F5F1)
val AzulBilletera = Color(0xFF3B82F6)
val Fondo = Color(0xFFF7F5FD)
val TextoPrincipal = Color(0xFF1D1B2E)
val TextoSecundario = Color(0xFF5E5A73)
val Coral = Color(0xFFE5484D)
val CoralClaro = Color(0xFFFFE5E5)
val Menta = Color(0xFF12A150)
val Ambar = Color(0xFFF5A524)

/** Degradado de la tarjeta de billetera y de las pantallas de bienvenida. */
val DegradadoBilletera = Brush.linearGradient(listOf(VioletaProfundo, Violeta, AzulBilletera))

private val Esquema = lightColorScheme(
    primary = Violeta,
    onPrimary = Color.White,
    primaryContainer = VioletaClaro,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Turquesa,
    onSecondary = Color.White,
    secondaryContainer = TurquesaClaro,
    onSecondaryContainer = Color(0xFF00403B),
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFEFEAFB),
    onSurfaceVariant = TextoSecundario,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFEFEAFB),
    error = Coral,
    onError = Color.White,
    errorContainer = CoralClaro,
    onErrorContainer = Color(0xFF5C0A0D),
    outline = Color(0xFFCFC8E6),
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun IrmexEstudianteTheme(content: @Composable () -> Unit) {
    // Siempre clara, para diferenciarla claramente de la app de administradores.
    MaterialTheme(colorScheme = Esquema, shapes = Formas, content = content)
}
