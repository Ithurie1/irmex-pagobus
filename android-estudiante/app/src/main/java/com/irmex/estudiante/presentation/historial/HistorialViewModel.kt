package com.irmex.estudiante.presentation.historial

import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Movimiento
import com.irmex.estudiante.domain.model.Tarjeta
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import com.irmex.estudiante.domain.usecase.ObservarMovimientosUseCase
import com.irmex.estudiante.domain.usecase.ObservarTarjetaUseCase
import com.irmex.estudiante.domain.usecase.RefrescarMovimientosUseCase
import com.irmex.estudiante.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    observarMovimientos: ObservarMovimientosUseCase,
    observarTarjeta: ObservarTarjetaUseCase,
    private val refrescarMovimientos: RefrescarMovimientosUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val movimientos: StateFlow<List<Movimiento>> = observarMovimientos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val tarjeta: StateFlow<Tarjeta?> = observarTarjeta()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarMovimientos() }
}
