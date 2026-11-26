package com.irmex.estudiante.presentation.inicio

import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Tarjeta
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import com.irmex.estudiante.domain.usecase.ObservarTarjetaUseCase
import com.irmex.estudiante.domain.usecase.ObserveSessionUseCase
import com.irmex.estudiante.domain.usecase.RefrescarTarjetaUseCase
import com.irmex.estudiante.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    observarTarjeta: ObservarTarjetaUseCase,
    observarSesion: ObserveSessionUseCase,
    private val refrescarTarjeta: RefrescarTarjetaUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val tarjeta: StateFlow<Tarjeta?> = observarTarjeta()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Primer nombre para el saludo. */
    val nombre: StateFlow<String> = observarSesion()
        .map { it?.nombre?.trim()?.substringBefore(' ').orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarTarjeta() }
}
