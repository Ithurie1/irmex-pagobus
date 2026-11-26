package com.irmex.estudiante.presentation.recargar

import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.model.Tarjeta
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import com.irmex.estudiante.domain.usecase.ObservarTarjetaUseCase
import com.irmex.estudiante.domain.usecase.RecargarSaldoUseCase
import com.irmex.estudiante.domain.usecase.RefrescarTarjetaUseCase
import com.irmex.estudiante.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecargarViewModel @Inject constructor(
    observarTarjeta: ObservarTarjetaUseCase,
    private val refrescarTarjeta: RefrescarTarjetaUseCase,
    private val recargarSaldo: RecargarSaldoUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val tarjeta: StateFlow<Tarjeta?> = observarTarjeta()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Avisa a la pantalla cuando la recarga salió bien (para limpiar el formulario y mostrar el resultado). */
    private val _recargaExitosa = Channel<Unit>(Channel.BUFFERED)
    val recargaExitosa = _recargaExitosa.receiveAsFlow()

    init {
        ejecutar(conCarga = false) { refrescarTarjeta() }
    }

    fun pagar(montoTexto: String, numero: String, vencimiento: String, cvc: String) {
        val monto = montoTexto.trim().toDoubleOrNull()
        ejecutar {
            val resultado = recargarSaldo(monto, numero, vencimiento, cvc)
            if (resultado is Outcome.Success) _recargaExitosa.trySend(Unit)
            resultado
        }
    }
}
