package com.irmex.estudiante.presentation.soporte

import androidx.lifecycle.viewModelScope
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.model.Reporte
import com.irmex.estudiante.domain.usecase.CrearReporteUseCase
import com.irmex.estudiante.domain.usecase.LogoutUseCase
import com.irmex.estudiante.domain.usecase.NombreArchivoUseCase
import com.irmex.estudiante.domain.usecase.ObservarReportesUseCase
import com.irmex.estudiante.domain.usecase.RefrescarReportesUseCase
import com.irmex.estudiante.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Estado del formulario "Nuevo reporte". */
data class FormularioReporte(
    val abierto: Boolean = false,
    val evidenciaUri: String? = null,
    val evidenciaNombre: String? = null,
    val enviando: Boolean = false,
    /** Error que se muestra dentro del formulario (el Snackbar quedaría detrás del diálogo). */
    val error: String? = null,
)

@HiltViewModel
class SoporteViewModel @Inject constructor(
    observarReportes: ObservarReportesUseCase,
    private val refrescarReportes: RefrescarReportesUseCase,
    private val crearReporte: CrearReporteUseCase,
    private val nombreArchivo: NombreArchivoUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val reportes: StateFlow<List<Reporte>> = observarReportes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formulario = MutableStateFlow(FormularioReporte())
    val formulario: StateFlow<FormularioReporte> = _formulario.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarReportes() }

    fun abrirFormulario() {
        _formulario.value = FormularioReporte(abierto = true)
    }

    fun cerrarFormulario() {
        if (!_formulario.value.enviando) _formulario.value = FormularioReporte()
    }

    fun elegirEvidencia(uri: String) {
        viewModelScope.launch {
            // Consultar el nombre del archivo es una operación de disco: se hace fuera del hilo principal.
            val nombre = withContext(Dispatchers.IO) { nombreArchivo(uri) }
            _formulario.update { it.copy(evidenciaUri = uri, evidenciaNombre = nombre, error = null) }
        }
    }

    fun quitarEvidencia() {
        _formulario.update { it.copy(evidenciaUri = null, evidenciaNombre = null) }
    }

    fun enviar(asunto: String, mensaje: String) {
        val actual = _formulario.value
        if (actual.enviando) return
        viewModelScope.launch {
            _formulario.update { it.copy(enviando = true, error = null) }
            when (val resultado = crearReporte(asunto, mensaje, actual.evidenciaUri)) {
                is Outcome.Success -> {
                    _formulario.value = FormularioReporte()
                    mostrar("Tu reporte fue enviado. Te avisaremos cuando tenga respuesta.")
                }
                is Outcome.Failure -> {
                    _formulario.update { it.copy(enviando = false, error = resultado.message) }
                    // Si la sesión expiró, además de avisar se cierra la sesión.
                    if (resultado.unauthorized) manejar(resultado, null)
                }
            }
        }
    }
}
