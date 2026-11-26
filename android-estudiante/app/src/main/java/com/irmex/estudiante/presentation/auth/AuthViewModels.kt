package com.irmex.estudiante.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.usecase.LoginUseCase
import com.irmex.estudiante.domain.usecase.RecuperarPasswordUseCase
import com.irmex.estudiante.domain.usecase.RegistrarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Estado común de las pantallas de acceso: carga y un mensaje para mostrar en un diálogo. */
data class AuthUiState(
    val cargando: Boolean = false,
    val titulo: String = "",
    val mensaje: String? = null,
    /** true si el diálogo es de éxito (por ejemplo, correo de recuperación enviado). */
    val exito: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun cerrarMensaje() = _state.update { it.copy(mensaje = null) }

    /** Recibe lo que el usuario tiene escrito en este momento (los campos viven en la pantalla). */
    fun iniciarSesion(correo: String, password: String) {
        if (_state.value.cargando) return
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }
            val resultado = login(correo, password)
            _state.update {
                when (resultado) {
                    // La navegación a la app ocurre sola cuando DataStore publica la sesión.
                    is Outcome.Success -> it.copy(cargando = false)
                    is Outcome.Failure -> it.copy(cargando = false, titulo = "No se pudo ingresar", mensaje = resultado.message)
                }
            }
        }
    }
}

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val registrar: RegistrarUseCase,
    private val login: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun cerrarMensaje() = _state.update { it.copy(mensaje = null) }

    fun crearCuenta(nombre: String, correo: String, password: String, fechaNacimiento: String) {
        if (_state.value.cargando) return
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }
            when (val registro = registrar(nombre, correo, password, fechaNacimiento)) {
                is Outcome.Failure -> _state.update {
                    it.copy(cargando = false, titulo = "No se pudo crear tu cuenta", mensaje = registro.message)
                }
                is Outcome.Success -> {
                    // Cuenta creada: se inicia sesión de una vez, sin pedir los datos otra vez.
                    val acceso = login(correo, password)
                    _state.update {
                        if (acceso is Outcome.Failure) {
                            it.copy(cargando = false, titulo = "Cuenta creada", mensaje = "Tu cuenta se creó, pero no pudimos iniciar sesión: ${acceso.message}", exito = true)
                        } else {
                            it.copy(cargando = false)
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class RecuperarViewModel @Inject constructor(
    private val recuperar: RecuperarPasswordUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun cerrarMensaje() = _state.update { it.copy(mensaje = null) }

    fun enviarEnlace(correo: String) {
        if (_state.value.cargando) return
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }
            val resultado = recuperar(correo)
            _state.update {
                when (resultado) {
                    is Outcome.Success -> it.copy(
                        cargando = false,
                        titulo = "Revisa tu correo",
                        mensaje = "Te enviamos un enlace para restablecer tu contraseña.",
                        exito = true,
                    )
                    is Outcome.Failure -> it.copy(cargando = false, titulo = "No se pudo enviar", mensaje = resultado.message, exito = false)
                }
            }
        }
    }
}
