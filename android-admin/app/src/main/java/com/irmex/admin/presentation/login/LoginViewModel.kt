package com.irmex.admin.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    /** Mensaje para el diálogo de error; null si no hay que mostrarlo. */
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onCorreoChange(valor: String) = _state.update { it.copy(correo = valor) }
    fun onPasswordChange(valor: String) = _state.update { it.copy(password = valor) }
    fun cerrarError() = _state.update { it.copy(error = null) }

    fun iniciarSesion() {
        val actual = _state.value
        if (actual.cargando) return
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }
            val resultado = login(actual.correo, actual.password)
            _state.update {
                when (resultado) {
                    // La navegación al Home ocurre sola cuando DataStore publica la sesión.
                    is Outcome.Success -> it.copy(cargando = false, password = "")
                    is Outcome.Failure -> it.copy(cargando = false, error = resultado.message)
                }
            }
        }
    }
}
