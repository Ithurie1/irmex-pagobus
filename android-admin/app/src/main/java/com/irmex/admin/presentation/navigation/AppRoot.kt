package com.irmex.admin.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.admin.presentation.home.HomeScreen
import com.irmex.admin.presentation.login.LoginScreen

/** Raíz de la app: muestra Login o Home según la sesión guardada en DataStore. */
@Composable
fun AppRoot(viewModel: AppViewModel = hiltViewModel()) {
    val haySesion by viewModel.haySesion.collectAsStateWithLifecycle()
    val usuario by viewModel.usuario.collectAsStateWithLifecycle()
    when (haySesion) {
        null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        true -> HomeScreen(nombre = usuario.first, rol = usuario.second, onCerrarSesion = viewModel::cerrarSesion)
        false -> LoginScreen()
    }
}
