package com.irmex.admin.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Box
import com.irmex.admin.presentation.estaciones.EstacionesScreen
import com.irmex.admin.presentation.renovaciones.RenovacionesScreen
import com.irmex.admin.presentation.tarjetas.TarjetasScreen
import com.irmex.admin.presentation.tickets.TicketsScreen

private enum class Pestana(val titulo: String, val icono: ImageVector) {
    RESUMEN("Resumen", Icons.Filled.Dashboard),
    TARJETAS("Tarjetas", Icons.Filled.CreditCard),
    ESTACIONES("Estaciones", Icons.Filled.DirectionsBus),
    TICKETS("Soporte", Icons.Filled.SupportAgent),
    RENOVACIONES("Renovaciones", Icons.Filled.ConfirmationNumber),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCerrarSesion: () -> Unit) {
    var seleccionada by rememberSaveable { mutableStateOf(Pestana.RESUMEN) }
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(seleccionada.titulo) },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                Pestana.entries.forEach { pestana ->
                    NavigationBarItem(
                        selected = pestana == seleccionada,
                        onClick = { seleccionada = pestana },
                        icon = { Icon(pestana.icono, contentDescription = pestana.titulo) },
                        label = { Text(pestana.titulo, maxLines = 1) },
                        alwaysShowLabel = false,
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (seleccionada) {
                Pestana.RESUMEN -> ResumenScreen(snackbar)
                Pestana.TARJETAS -> TarjetasScreen(snackbar)
                Pestana.ESTACIONES -> EstacionesScreen(snackbar)
                Pestana.TICKETS -> TicketsScreen(snackbar)
                Pestana.RENOVACIONES -> RenovacionesScreen(snackbar)
            }
        }
    }
}
