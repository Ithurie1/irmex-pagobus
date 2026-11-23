package com.irmex.admin.presentation.estaciones

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Estacion
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarEstacionesUseCase
import com.irmex.admin.domain.usecase.RefrescarEstacionesUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EstacionesViewModel @Inject constructor(
    observarEstaciones: ObservarEstacionesUseCase,
    private val refrescarEstaciones: RefrescarEstacionesUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val estaciones: StateFlow<List<Estacion>> = observarEstaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarEstaciones() }
}
