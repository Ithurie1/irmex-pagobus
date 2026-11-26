package com.irmex.estudiante.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.irmex.estudiante.presentation.auth.AuthHost

/** Raíz de la app: muestra el acceso (login/registro) o la app principal según la sesión guardada. */
@Composable
fun AppRoot(viewModel: AppViewModel = hiltViewModel()) {
    val haySesion by viewModel.haySesion.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (haySesion) {
            null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            true -> MainScreen(onCerrarSesion = viewModel::cerrarSesion)
            false -> AuthHost()
        }
    }
}
