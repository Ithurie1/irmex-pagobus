package com.irmex.estudiante.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.irmex.estudiante.presentation.historial.HistorialScreen
import com.irmex.estudiante.presentation.inicio.InicioScreen
import com.irmex.estudiante.presentation.perfil.PerfilScreen
import com.irmex.estudiante.presentation.recargar.RecargarScreen
import com.irmex.estudiante.presentation.renovar.RenovarScreen
import com.irmex.estudiante.presentation.soporte.SoporteScreen

/** Las cinco pestañas de la barra inferior. */
private enum class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    INICIO("inicio", "Inicio", Icons.Filled.Home),
    RECARGAR("recargar", "Recargar", Icons.Filled.AddCard),
    HISTORIAL("historial", "Historial", Icons.Filled.History),
    REPORTES("reportes", "Reportes", Icons.Filled.Forum),
    PERFIL("perfil", "Perfil", Icons.Filled.Person),
}

/** Pantalla que se abre encima de las pestañas (sin barra inferior). */
private const val RUTA_RENOVAR = "renovar"

@Composable
fun MainScreen(onCerrarSesion: () -> Unit) {
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarBarra = Destino.entries.any { it.ruta == rutaActual }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    Destino.entries.forEach { destino ->
                        NavigationBarItem(
                            selected = destino.ruta == rutaActual,
                            onClick = { nav.irAPestana(destino.ruta) },
                            icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                            label = { Text(destino.titulo) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Destino.INICIO.ruta,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destino.INICIO.ruta) {
                InicioScreen(
                    snackbar = snackbar,
                    onRecargar = { nav.irAPestana(Destino.RECARGAR.ruta) },
                    onHistorial = { nav.irAPestana(Destino.HISTORIAL.ruta) },
                    onAyuda = { nav.irAPestana(Destino.REPORTES.ruta) },
                    onRenovar = { nav.navigate(RUTA_RENOVAR) },
                )
            }
            composable(Destino.RECARGAR.ruta) { RecargarScreen(snackbar) }
            composable(Destino.HISTORIAL.ruta) { HistorialScreen(snackbar) }
            composable(Destino.REPORTES.ruta) { SoporteScreen(snackbar) }
            composable(Destino.PERFIL.ruta) { PerfilScreen(snackbar, onCerrarSesion) }
            composable(RUTA_RENOVAR) { RenovarScreen(snackbar, onVolver = { nav.popBackStack() }) }
        }
    }
}

/** Cambia de pestaña conservando el estado de cada una. */
private fun NavController.irAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
