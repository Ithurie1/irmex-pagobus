package com.irmex.admin.presentation.tickets

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Ticket
import com.irmex.admin.domain.usecase.EliminarTicketUseCase
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.MarcarTicketImportanteUseCase
import com.irmex.admin.domain.usecase.MarcarTicketLeidoUseCase
import com.irmex.admin.domain.usecase.ObservarTicketsUseCase
import com.irmex.admin.domain.usecase.RefrescarTicketsUseCase
import com.irmex.admin.domain.usecase.ResponderTicketUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Carpetas del buzón, igual que en la web. */
enum class Carpeta(val titulo: String, val textoVacio: String) {
    BANDEJA("Bandeja", "No hay mensajes sin leer."),
    DESTACADOS("Destacados", "No hay mensajes destacados."),
    LEIDOS("Leídos", "No hay mensajes leídos."),
}

@HiltViewModel
class TicketsViewModel @Inject constructor(
    observarTickets: ObservarTicketsUseCase,
    private val refrescarTickets: RefrescarTicketsUseCase,
    private val responderTicket: ResponderTicketUseCase,
    private val marcarImportante: MarcarTicketImportanteUseCase,
    private val marcarLeido: MarcarTicketLeidoUseCase,
    private val eliminarTicket: EliminarTicketUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    private val todos: StateFlow<List<Ticket>> = observarTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _carpeta = MutableStateFlow(Carpeta.BANDEJA)
    val carpeta: StateFlow<Carpeta> = _carpeta.asStateFlow()

    /** Tickets de la carpeta elegida. */
    val tickets: StateFlow<List<Ticket>> = combine(todos, _carpeta) { lista, carpeta ->
        when (carpeta) {
            Carpeta.BANDEJA -> lista.filter { !it.revisado }
            Carpeta.DESTACADOS -> lista.filter { it.importante }
            Carpeta.LEIDOS -> lista.filter { it.revisado }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Cantidad de mensajes sin leer (se muestra en la pestaña de la bandeja). */
    val sinLeer: StateFlow<Int> = todos.map { lista -> lista.count { !it.revisado } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Ticket que se está respondiendo (abre el diálogo); null si ninguno. */
    private val _enRespuesta = MutableStateFlow<Ticket?>(null)
    val enRespuesta: StateFlow<Ticket?> = _enRespuesta.asStateFlow()

    /** Ticket pendiente de confirmar su borrado; null si ninguno. */
    private val _porBorrar = MutableStateFlow<Ticket?>(null)
    val porBorrar: StateFlow<Ticket?> = _porBorrar.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarTickets() }

    fun elegirCarpeta(carpeta: Carpeta) {
        _carpeta.value = carpeta
    }

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

    fun alternarLeido(ticket: Ticket) =
        ejecutar(conCarga = false) { marcarLeido(ticket.id, !ticket.revisado) }

    fun pedirBorrado(ticket: Ticket) {
        _porBorrar.value = ticket
    }

    fun cancelarBorrado() {
        _porBorrar.value = null
    }

    fun confirmarBorrado() {
        val ticket = _porBorrar.value ?: return
        _porBorrar.value = null
        ejecutar(mensajeExito = "Ticket eliminado.") { eliminarTicket(ticket.id) }
    }
}
