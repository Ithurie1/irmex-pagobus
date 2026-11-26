package com.irmex.admin.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow

/** Muestra cada mensaje del ViewModel como Snackbar. */
@Composable
fun MensajesEffect(mensajes: Flow<String>, snackbar: SnackbarHostState) {
    LaunchedEffect(mensajes) {
        mensajes.collect { snackbar.showSnackbar(it) }
    }
}

/** Lista con "deslizar para actualizar" y estado vacío. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ListaRefrescable(
    items: List<T>,
    cargando: Boolean,
    textoVacio: String,
    onRefrescar: () -> Unit,
    clave: (T) -> Any,
    contenido: @Composable (T) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = cargando,
        onRefresh = onRefrescar,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (items.isEmpty()) {
            // LazyColumn vacía para que el gesto de refrescar funcione también sin datos.
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(32.dp)) {
                item {
                    Box(Modifier.fillMaxSize().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (cargando) "Cargando…" else textoVacio,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items, key = clave) { contenido(it) }
            }
        }
    }
}

fun formatoMoneda(monto: Double): String = String.format(java.util.Locale.US, "$%,.2f", monto)

/** Etiqueta de color según el estatus (activa/operativa en verde, el resto en rojo). */
@Composable
fun EstatusChip(estatus: String) {
    val positivo = estatus.lowercase() in setOf("activa", "operativa", "activo", "aprobada")
    Text(
        text = estatus.ifBlank { "—" },
        style = MaterialTheme.typography.labelLarge,
        color = if (positivo) com.irmex.admin.presentation.theme.Verde else com.irmex.admin.presentation.theme.Rojo,
    )
}

/** Roles que el gestor de privilegios puede asignar (mismos valores que el backend). */
val ROLES_ASIGNABLES = listOf("estudiante", "admin", "superadmin")

fun etiquetaRol(rol: String): String = when (rol.lowercase()) {
    "estudiante" -> "Estudiante"
    "admin" -> "Administrador"
    "superadmin" -> "Super Admin"
    else -> rol
}
