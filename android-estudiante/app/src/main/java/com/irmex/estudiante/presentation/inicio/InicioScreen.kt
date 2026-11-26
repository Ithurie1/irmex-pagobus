package com.irmex.estudiante.presentation.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.domain.model.Tarjeta
import com.irmex.estudiante.presentation.common.DialogoCerrarSesion
import com.irmex.estudiante.presentation.common.MensajesEffect
import com.irmex.estudiante.presentation.common.agruparTarjeta
import com.irmex.estudiante.presentation.common.formatoMoneda
import com.irmex.estudiante.presentation.theme.Ambar
import com.irmex.estudiante.presentation.theme.DegradadoBilletera
import com.irmex.estudiante.presentation.theme.Menta

/** Inicio: la billetera con el saldo y los accesos rápidos a lo más usado. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    snackbar: SnackbarHostState,
    onRecargar: () -> Unit,
    onHistorial: () -> Unit,
    onAyuda: () -> Unit,
    onRenovar: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: InicioViewModel = hiltViewModel(),
) {
    val tarjeta by viewModel.tarjeta.collectAsStateWithLifecycle()
    val nombre by viewModel.nombre.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    MensajesEffect(viewModel.mensajes, snackbar)

    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    if (confirmarSalida) {
        DialogoCerrarSesion(
            onConfirmar = { confirmarSalida = false; onCerrarSesion() },
            onCancelar = { confirmarSalida = false },
        )
    }

    PullToRefreshBox(isRefreshing = cargando, onRefresh = viewModel::refrescar, modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (nombre.isBlank()) "Hola" else "Hola, $nombre",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Este es el resumen de tu tarjeta IrMex.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { confirmarSalida = true }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                }
            }

            val actual = tarjeta
            if (actual == null) {
                SinTarjeta(cargando)
            } else {
                if (!actual.estaActiva || actual.requiereRenovacion) AvisoRenovacion(actual, onRenovar)
                TarjetaBilletera(actual)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                AccionRapida("Recargar", Icons.Filled.AddCard, onRecargar)
                AccionRapida("Historial", Icons.Filled.History, onHistorial)
                AccionRapida("Renovar", Icons.Filled.Description, onRenovar)
                AccionRapida("Ayuda", Icons.Filled.Forum, onAyuda)
            }
        }
    }
}

@Composable
private fun SinTarjeta(cargando: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            if (cargando) "Buscando tu tarjeta…" else "Aún no tienes una tarjeta asignada. Desliza hacia abajo para actualizar.",
            modifier = Modifier.padding(20.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AvisoRenovacion(tarjeta: Tarjeta, onRenovar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                if (tarjeta.estaActiva) "Tu beneficio de estudiante está por vencer" else "Tu tarjeta está inactiva",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                if (tarjeta.estaActiva) {
                    "Renuévalo antes del ${tarjeta.vigencia} subiendo tu comprobante de estudios."
                } else {
                    "Sube tu comprobante de estudios o INE para que un administrador la active."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            TextButton(onClick = onRenovar) { Text("Renovar ahora") }
        }
    }
}

/** La billetera: degradado morado con el saldo, el número enmascarado y el estatus. */
@Composable
private fun TarjetaBilletera(tarjeta: Tarjeta) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .background(DegradadoBilletera, RoundedCornerShape(28.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Mi Billetera IrMex", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.titleSmall)
            Text(
                tarjeta.estatus.ifBlank { "—" },
                style = MaterialTheme.typography.labelLarge,
                color = if (tarjeta.estaActiva) Menta else Ambar,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
        Text("Saldo disponible", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.bodySmall)
        Text(formatoMoneda(tarjeta.saldo), color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
        if (tarjeta.saldo < 20.0) {
            Text("Saldo bajo: te conviene recargar", color = Ambar, style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Número de tarjeta", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.bodySmall)
                Text(
                    if (visible) agruparTarjeta(tarjeta.numero) else "•••• •••• •••• ${tarjeta.ultimosDigitos}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Ocultar número" else "Mostrar número",
                    tint = Color.White,
                )
            }
        }
        if (tarjeta.vigencia.isNotBlank()) {
            Text("Vigencia: ${tarjeta.vigencia}", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AccionRapida(texto: String, icono: ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FilledTonalIconButton(onClick = onClick, modifier = Modifier.padding(2.dp)) {
            Icon(icono, contentDescription = texto)
        }
        Text(texto, style = MaterialTheme.typography.labelMedium)
    }
}
