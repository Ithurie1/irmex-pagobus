package com.irmex.admin.presentation.tarjetas

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.domain.model.Usuario
import com.irmex.admin.domain.usecase.AjustarVigenciaUseCase
import com.irmex.admin.domain.usecase.AlternarEstatusTarjetaUseCase
import com.irmex.admin.domain.usecase.BorrarTarjetaUseCase
import com.irmex.admin.domain.usecase.CrearTarjetaUseCase
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarTarjetasUseCase
import com.irmex.admin.domain.usecase.ObservarUsuariosUseCase
import com.irmex.admin.domain.usecase.RecargaManualUseCase
import com.irmex.admin.domain.usecase.RefrescarTarjetasUseCase
import com.irmex.admin.domain.usecase.RefrescarUsuariosUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Diálogo abierto en el módulo de tarjetas (null = ninguno). */
sealed interface DialogoTarjeta {
    data object Alta : DialogoTarjeta
    data class Vigencia(val tarjeta: Tarjeta) : DialogoTarjeta
    data class Recarga(val tarjeta: Tarjeta) : DialogoTarjeta
    data class Estatus(val tarjeta: Tarjeta) : DialogoTarjeta
    data class Borrar(val tarjeta: Tarjeta) : DialogoTarjeta
}

@HiltViewModel
class TarjetasViewModel @Inject constructor(
    observarTarjetas: ObservarTarjetasUseCase,
    observarUsuarios: ObservarUsuariosUseCase,
    private val refrescarTarjetas: RefrescarTarjetasUseCase,
    private val refrescarUsuarios: RefrescarUsuariosUseCase,
    private val crearTarjeta: CrearTarjetaUseCase,
    private val ajustarVigencia: AjustarVigenciaUseCase,
    private val recargaManual: RecargaManualUseCase,
    private val alternarEstatus: AlternarEstatusTarjetaUseCase,
    private val borrarTarjeta: BorrarTarjetaUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    val tarjetas: StateFlow<List<Tarjeta>> = observarTarjetas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Estudiantes a los que se puede asignar una tarjeta nueva. */
    val estudiantes: StateFlow<List<Usuario>> = observarUsuarios()
        .map { lista -> lista.filter { it.rol.lowercase() == "estudiante" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _dialogo = MutableStateFlow<DialogoTarjeta?>(null)
    val dialogo: StateFlow<DialogoTarjeta?> = _dialogo.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarTarjetas() }

    fun abrir(dialogo: DialogoTarjeta) {
        _dialogo.value = dialogo
        // Para el alta se necesita la lista de estudiantes al día.
        if (dialogo is DialogoTarjeta.Alta) ejecutar(conCarga = false) { refrescarUsuarios() }
    }

    fun cerrar() {
        _dialogo.value = null
    }

    fun confirmarAlta(numero: String, saldoTexto: String, estatus: String, usuarioId: Int?) {
        val saldo = saldoTexto.trim().ifEmpty { "0" }.toIntOrNull()
        if (saldo == null) {
            mostrar("El saldo inicial debe ser un número entero.")
            return
        }
        cerrar()
        ejecutar(mensajeExito = "Tarjeta registrada.") { crearTarjeta(numero.trim(), saldo, estatus, usuarioId) }
    }

    fun confirmarVigencia(tarjeta: Tarjeta, diasTexto: String) {
        val dias = diasTexto.trim().toIntOrNull()
        if (dias == null) {
            mostrar("Escribe un número entero de días.")
            return
        }
        cerrar()
        ejecutar(mensajeExito = "Vigencia actualizada.") { ajustarVigencia(tarjeta.id, dias) }
    }

    fun confirmarRecarga(tarjeta: Tarjeta, montoTexto: String, nip: String) {
        val monto = montoTexto.trim().toDoubleOrNull()
        if (monto == null) {
            mostrar("Escribe un monto válido.")
            return
        }
        cerrar()
        ejecutar(mensajeExito = "NIP aceptado. Fondos agregados.") { recargaManual(tarjeta.id, monto, nip) }
    }

    fun confirmarEstatus(tarjeta: Tarjeta, nip: String) {
        cerrar()
        ejecutar(mensajeExito = "Estatus de la tarjeta actualizado.") { alternarEstatus(tarjeta.id, nip) }
    }

    fun confirmarBorrado(tarjeta: Tarjeta, nip: String) {
        cerrar()
        ejecutar(mensajeExito = "Tarjeta eliminada.") { borrarTarjeta(tarjeta.id, nip) }
    }
}
