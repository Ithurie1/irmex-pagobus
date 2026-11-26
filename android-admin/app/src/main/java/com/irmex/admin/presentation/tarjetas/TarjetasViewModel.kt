package com.irmex.admin.presentation.tarjetas

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarTarjetasUseCase
import com.irmex.admin.domain.usecase.RefrescarTarjetasUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TarjetasViewModel @Inject constructor(
    observarTarjetas: ObservarTarjetasUseCase,
    private val refrescarTarjetas: RefrescarTarjetasUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val tarjetas: StateFlow<List<Tarjeta>> = observarTarjetas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarTarjetas() }
}
