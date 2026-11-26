package com.irmex.estudiante.presentation.perfil

import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Perfil
import com.irmex.estudiante.domain.usecase.GuardarPerfilUseCase
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import com.irmex.estudiante.domain.usecase.ObservarPerfilUseCase
import com.irmex.estudiante.domain.usecase.RefrescarPerfilUseCase
import com.irmex.estudiante.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    observarPerfil: ObservarPerfilUseCase,
    private val refrescarPerfil: RefrescarPerfilUseCase,
    private val guardarPerfil: GuardarPerfilUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val perfil: StateFlow<Perfil?> = observarPerfil()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarPerfil() }

    fun guardar(nombre: String, curp: String, calle: String, numero: String, colonia: String, cp: String) {
        val actual = perfil.value ?: return
        val nuevo = actual.copy(
            nombre = nombre.trim(),
            curp = curp.trim().uppercase(),
            calle = calle.trim(),
            numero = numero.trim(),
            colonia = colonia.trim(),
            cp = cp.trim(),
        )
        ejecutar(mensajeExito = "Perfil actualizado.") { guardarPerfil(nuevo) }
    }
}
