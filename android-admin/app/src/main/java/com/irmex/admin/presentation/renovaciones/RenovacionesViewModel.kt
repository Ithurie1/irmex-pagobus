package com.irmex.admin.presentation.renovaciones

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarRenovacionesUseCase
import com.irmex.admin.domain.usecase.RefrescarRenovacionesUseCase
import com.irmex.admin.domain.usecase.ResolverRenovacionUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RenovacionesViewModel @Inject constructor(
    observarRenovaciones: ObservarRenovacionesUseCase,
    private val refrescarRenovaciones: RefrescarRenovacionesUseCase,
    private val resolverRenovacion: ResolverRenovacionUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val renovaciones: StateFlow<List<Renovacion>> = observarRenovaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarRenovaciones() }

    fun aprobar(renovacion: Renovacion) =
        ejecutar(mensajeExito = "Renovación aprobada.") { resolverRenovacion(renovacion.id, aprobar = true) }

    fun rechazar(renovacion: Renovacion) =
        ejecutar(mensajeExito = "Renovación rechazada.") { resolverRenovacion(renovacion.id, aprobar = false) }
}
