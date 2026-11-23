package com.irmex.admin.presentation.home

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.ResumenGeneral
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObtenerResumenUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumenViewModel @Inject constructor(
    private val obtenerResumen: ObtenerResumenUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    // Se conserva el último resumen obtenido mientras la app siga abierta.
    private val _resumen = MutableStateFlow<ResumenGeneral?>(null)
    val resumen: StateFlow<ResumenGeneral?> = _resumen.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar {
        obtenerResumen().also { resultado ->
            if (resultado is Outcome.Success) _resumen.value = resultado.data
        }
    }
}
