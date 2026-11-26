package com.irmex.admin.presentation.renovaciones

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarRenovacionesUseCase
import com.irmex.admin.domain.usecase.ObtenerDocumentoUseCase
import com.irmex.admin.domain.usecase.RefrescarRenovacionesUseCase
import com.irmex.admin.domain.usecase.ResolverRenovacionUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Estado del visor de documentos (null = cerrado). */
sealed interface DocumentoUi {
    val titulo: String

    data class Cargando(override val titulo: String) : DocumentoUi
    data class Listo(override val titulo: String, val paginas: List<ImageBitmap>) : DocumentoUi
}

@HiltViewModel
class RenovacionesViewModel @Inject constructor(
    observarRenovaciones: ObservarRenovacionesUseCase,
    private val refrescarRenovaciones: RefrescarRenovacionesUseCase,
    private val resolverRenovacion: ResolverRenovacionUseCase,
    private val obtenerDocumento: ObtenerDocumentoUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val renovaciones: StateFlow<List<Renovacion>> = observarRenovaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _documento = MutableStateFlow<DocumentoUi?>(null)
    val documento: StateFlow<DocumentoUi?> = _documento.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarRenovaciones() }

    fun aprobar(renovacion: Renovacion) =
        ejecutar(mensajeExito = "Renovación aprobada.") { resolverRenovacion(renovacion.id, aprobar = true) }

    fun rechazar(renovacion: Renovacion) =
        ejecutar(mensajeExito = "Renovación rechazada.") { resolverRenovacion(renovacion.id, aprobar = false) }

    fun verDocumento(renovacion: Renovacion) {
        val titulo = "${renovacion.nombreUsuario} · ${renovacion.nombreArchivo}"
        _documento.value = DocumentoUi.Cargando(titulo)
        ejecutar(conCarga = false) {
            when (val resultado = obtenerDocumento(renovacion.nombreArchivo)) {
                is Outcome.Failure -> {
                    _documento.value = null
                    resultado
                }
                is Outcome.Success -> {
                    // Decodificar imágenes es trabajo pesado: se hace fuera del hilo principal.
                    val paginas = withContext(Dispatchers.Default) {
                        resultado.data.paginas.mapNotNull { decodificarPagina(it) }
                    }
                    if (paginas.isEmpty()) {
                        _documento.value = null
                        Outcome.Failure("No se pudo mostrar el documento (formato no compatible).")
                    } else {
                        _documento.value = DocumentoUi.Listo(titulo, paginas)
                        Outcome.Success(Unit)
                    }
                }
            }
        }
    }

    fun cerrarDocumento() {
        _documento.value = null
    }
}
