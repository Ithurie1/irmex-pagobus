package com.irmex.estudiante.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Lógica común: indicador de carga, mensajes (Snackbar) y cierre de sesión cuando el token expira.
 * Los Composables solo observan estos flujos; no contienen lógica.
 */
abstract class BaseViewModel(private val logout: LogoutUseCase) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajes = Channel<String>(Channel.BUFFERED)
    val mensajes = _mensajes.receiveAsFlow()

    protected fun mostrar(mensaje: String) {
        _mensajes.trySend(mensaje)
    }

    /** Ejecuta [accion] mostrando el indicador de carga y reportando fallos como mensajes. */
    protected fun ejecutar(
        conCarga: Boolean = true,
        mensajeExito: String? = null,
        accion: suspend () -> Outcome<*>,
    ) {
        viewModelScope.launch {
            if (conCarga) _cargando.value = true
            val resultado = accion()
            if (conCarga) _cargando.value = false
            manejar(resultado, mensajeExito)
        }
    }

    protected suspend fun manejar(resultado: Outcome<*>, mensajeExito: String?) {
        when (resultado) {
            is Outcome.Success -> mensajeExito?.let(::mostrar)
            is Outcome.Failure -> {
                mostrar(resultado.message)
                if (resultado.unauthorized) logout()
            }
        }
    }
}
