package com.irmex.admin.presentation.tickets

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Ticket
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.MarcarTicketImportanteUseCase
import com.irmex.admin.domain.usecase.ObservarTicketsUseCase
import com.irmex.admin.domain.usecase.RefrescarTicketsUseCase
import com.irmex.admin.domain.usecase.ResponderTicketUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    observarTickets: ObservarTicketsUseCase,
    private val refrescarTickets: RefrescarTicketsUseCase,
    private val responderTicket: ResponderTicketUseCase,
    private val marcarImportante: MarcarTicketImportanteUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val tickets: StateFlow<List<Ticket>> = observarTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Ticket que se está respondiendo (abre el diálogo); null si ninguno. */
    private val _enRespuesta = MutableStateFlow<Ticket?>(null)
    val enRespuesta: StateFlow<Ticket?> = _enRespuesta.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarTickets() }

    fun abrirRespuesta(ticket: Ticket) {
        _enRespuesta.value = ticket
    }

    fun cerrarRespuesta() {
        _enRespuesta.value = null
    }

    fun enviarRespuesta(texto: String) {
        val ticket = _enRespuesta.value ?: return
        _enRespuesta.value = null
        ejecutar(mensajeExito = "Respuesta enviada.") { responderTicket(ticket.id, texto) }
    }

    fun alternarImportante(ticket: Ticket) =
        ejecutar(conCarga = false) { marcarImportante(ticket.id, !ticket.importante) }
}
