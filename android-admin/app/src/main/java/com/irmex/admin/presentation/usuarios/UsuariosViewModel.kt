package com.irmex.admin.presentation.usuarios

import androidx.lifecycle.viewModelScope
import com.irmex.admin.domain.model.Usuario
import com.irmex.admin.domain.usecase.CambiarRolUseCase
import com.irmex.admin.domain.usecase.DarDeBajaUsuarioUseCase
import com.irmex.admin.domain.usecase.LogoutUseCase
import com.irmex.admin.domain.usecase.ObservarUsuariosUseCase
import com.irmex.admin.domain.usecase.RefrescarUsuariosUseCase
import com.irmex.admin.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Cambio de rol a la espera del NIP de autorización. */
data class CambioPendiente(val usuario: Usuario, val nuevoRol: String)

@HiltViewModel
class UsuariosViewModel @Inject constructor(
    observarUsuarios: ObservarUsuariosUseCase,
    private val refrescarUsuarios: RefrescarUsuariosUseCase,
    private val darDeBajaUsuario: DarDeBajaUsuarioUseCase,
    private val cambiarRol: CambiarRolUseCase,
    logout: LogoutUseCase,
) : BaseViewModel(logout) {

    private val _consulta = MutableStateFlow("")
    val consulta: StateFlow<String> = _consulta.asStateFlow()

    /** Usuarios filtrados por nombre o correo (búsqueda local sobre la caché). */
    val usuarios: StateFlow<List<Usuario>> =
        combine(observarUsuarios(), _consulta) { lista, texto ->
            val q = texto.trim().lowercase()
            if (q.isEmpty()) lista
            else lista.filter { it.nombre.lowercase().contains(q) || it.correo.lowercase().contains(q) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _usuarioPorBaja = MutableStateFlow<Usuario?>(null)
    val usuarioPorBaja: StateFlow<Usuario?> = _usuarioPorBaja.asStateFlow()

    private val _cambioPendiente = MutableStateFlow<CambioPendiente?>(null)
    val cambioPendiente: StateFlow<CambioPendiente?> = _cambioPendiente.asStateFlow()

    init {
        refrescar()
    }

    fun refrescar() = ejecutar { refrescarUsuarios() }

    fun onConsultaChange(texto: String) {
        _consulta.value = texto
    }

    // --- Dar de baja ---
    fun pedirBaja(usuario: Usuario) {
        _usuarioPorBaja.value = usuario
    }

    fun cancelarBaja() {
        _usuarioPorBaja.value = null
    }

    fun confirmarBaja() {
        val usuario = _usuarioPorBaja.value ?: return
        _usuarioPorBaja.value = null
        ejecutar(mensajeExito = "${usuario.nombre} fue dado de baja.") { darDeBajaUsuario(usuario.id) }
    }

    // --- Cambio de rol (requiere NIP) ---
    fun pedirCambioRol(usuario: Usuario, nuevoRol: String) {
        if (nuevoRol.lowercase() == usuario.rol.lowercase()) return
        _cambioPendiente.value = CambioPendiente(usuario, nuevoRol)
    }

    fun cancelarCambioRol() {
        _cambioPendiente.value = null
    }

    fun confirmarCambioRol(nip: String) {
        val cambio = _cambioPendiente.value ?: return
        _cambioPendiente.value = null
        ejecutar(mensajeExito = "Rol de ${cambio.usuario.nombre} actualizado a ${cambio.nuevoRol}.") {
            cambiarRol(cambio.usuario.id, cambio.nuevoRol, nip)
        }
    }
}
