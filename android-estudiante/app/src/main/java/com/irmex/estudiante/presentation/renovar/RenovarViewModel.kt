package com.irmex.estudiante.presentation.renovar

import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import com.irmex.estudiante.domain.usecase.NombreArchivoUseCase
import com.irmex.estudiante.domain.usecase.SolicitarRenovacionUseCase
import com.irmex.estudiante.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class RenovarUiState(
    val archivoUri: String? = null,
    val archivoNombre: String? = null,
    val enviado: Boolean = false,
)

@HiltViewModel
class RenovarViewModel @Inject constructor(
    private val solicitarRenovacion: SolicitarRenovacionUseCase,
    private val nombreArchivo: NombreArchivoUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    private val _state = MutableStateFlow(RenovarUiState())
    val state: StateFlow<RenovarUiState> = _state.asStateFlow()

    fun elegirArchivo(uri: String) {
        viewModelScope.launch {
            // Consultar el nombre del archivo es una operación de disco: se hace fuera del hilo principal.
            val nombre = withContext(Dispatchers.IO) { nombreArchivo(uri) }
            _state.value = RenovarUiState(archivoUri = uri, archivoNombre = nombre)
        }
    }

    fun enviar() {
        val uri = _state.value.archivoUri
        ejecutar {
            val resultado = solicitarRenovacion(uri)
            if (resultado is Outcome.Success) _state.value = _state.value.copy(enviado = true)
            resultado
        }
    }
}
