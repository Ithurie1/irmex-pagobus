package com.irmex.estudiante.presentation.common

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

/** Agrupa un número de tarjeta de 16 dígitos de 4 en 4. */
fun agruparTarjeta(numero: String): String = numero.chunked(4).joinToString(" ")

/** Inserta la diagonal del vencimiento mientras se escribe: 1226 -> 12/26. */
fun formatoVencimiento(texto: String): String {
    val digitos = texto.filter(Char::isDigit).take(4)
    return if (digitos.length > 2) digitos.substring(0, 2) + "/" + digitos.substring(2) else digitos
}

/** Inserta los guiones de la fecha mientras se escribe: 20040325 -> 2004-03-25. */
fun formatoFecha(texto: String): String {
    val digitos = texto.filter(Char::isDigit).take(8)
    return buildString {
        digitos.forEachIndexed { indice, c ->
            if (indice == 4 || indice == 6) append('-')
            append(c)
        }
    }
}
