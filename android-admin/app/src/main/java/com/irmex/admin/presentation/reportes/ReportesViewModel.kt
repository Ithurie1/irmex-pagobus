package com.irmex.admin.presentation.reportes

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.AccesoLog
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.ResumenGeneral
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarAccesosUseCase
import com.irmex.admin.domain.usecase.ObservarResumenUseCase
import com.irmex.admin.domain.usecase.RefrescarAccesosUseCase
import com.irmex.admin.domain.usecase.RefrescarResumenUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportesViewModel @Inject constructor(
    observarResumen: ObservarResumenUseCase,
    observarAccesos: ObservarAccesosUseCase,
    private val refrescarResumen: RefrescarResumenUseCase,
    private val refrescarAccesos: RefrescarAccesosUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val resumen: StateFlow<ResumenGeneral?> = observarResumen()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val accesos: StateFlow<List<AccesoLog>> = observarAccesos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refrescar()
    }

    fun refrescar() = ejecutar {
        val resultadoResumen = refrescarResumen()
        val resultadoAccesos = refrescarAccesos()
        // Se reporta un solo mensaje: el primer fallo, o éxito si ambos salieron bien.
        if (resultadoResumen is Outcome.Failure) resultadoResumen else resultadoAccesos
    }
}
