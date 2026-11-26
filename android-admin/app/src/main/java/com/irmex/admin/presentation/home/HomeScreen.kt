package com.irmex.admin.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.irmex.admin.domain.model.Roles
import com.irmex.admin.presentation.perfil.PerfilScreen
import com.irmex.admin.presentation.renovaciones.RenovacionesScreen
import com.irmex.admin.presentation.tarjetas.TarjetasScreen
import com.irmex.admin.presentation.tickets.TicketsScreen
import com.irmex.admin.presentation.usuarios.EstatusUsuariosScreen
import com.irmex.admin.presentation.usuarios.PrivilegiosScreen

private const val RUTA_MENU = "menu"

/** Navegación del panel: menú de módulos + una pantalla por módulo con flecha de regreso. */
@Composable
fun HomeScreen(nombre: String, rol: String, onCerrarSesion: () -> Unit) {
    val nav = rememberNavController()
    // El gestor de privilegios solo se ofrece a superadmin (igual que en la web).
    val visibles = Modulo.entries.filter { !it.soloSuperadmin || Roles.esSuperadmin(rol) }

    NavHost(navController = nav, startDestination = RUTA_MENU) {
        composable(RUTA_MENU) {
            MenuScreen(
                nombre = nombre,
                rol = rol,
                modulos = visibles,
                onAbrir = { nav.navigate(it.ruta) },
                onCerrarSesion = onCerrarSesion,
            )
        }
        visibles.forEach { modulo ->
            composable(modulo.ruta) {
                ModuloScaffold(titulo = modulo.titulo, onVolver = { nav.popBackStack() }) { snackbar ->
                    when (modulo) {
                        Modulo.TARJETAS -> TarjetasScreen(snackbar)
                        Modulo.ESTATUS_USUARIOS -> EstatusUsuariosScreen(snackbar)
                        Modulo.PRIVILEGIOS -> PrivilegiosScreen(snackbar)
                        Modulo.REPORTES -> ResumenScreen(snackbar)
                        Modulo.DOCUMENTOS -> RenovacionesScreen(snackbar)
                        Modulo.SOPORTE -> TicketsScreen(snackbar)
                        Modulo.PERFIL -> PerfilScreen(snackbar)
                    }
                }
            }
        }
    }
}

/** Marco común de cada módulo: barra superior con regreso y espacio para Snackbars. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuloScaffold(
    titulo: String,
    onVolver: () -> Unit,
    contenido: @Composable (SnackbarHostState) -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al menú")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.padding(padding)) { contenido(snackbar) }
    }
}
