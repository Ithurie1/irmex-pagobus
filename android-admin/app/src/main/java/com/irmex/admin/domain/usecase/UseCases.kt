package com.irmex.admin.domain.usecase

import com.irmex.admin.domain.model.Documento
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.Perfil
import com.irmex.admin.domain.model.Roles
import com.irmex.admin.domain.model.Session
import com.irmex.admin.domain.repository.AccesoRepository
import com.irmex.admin.domain.repository.AuthRepository
import com.irmex.admin.domain.repository.DocumentoRepository
import com.irmex.admin.domain.repository.PerfilRepository
import com.irmex.admin.domain.repository.RenovacionRepository
import com.irmex.admin.domain.repository.ResumenRepository
import com.irmex.admin.domain.repository.TarjetaRepository
import com.irmex.admin.domain.repository.TicketRepository
import com.irmex.admin.domain.repository.UsuarioRepository
import javax.inject.Inject

const val MENSAJE_SOLO_ESTUDIANTES =
    "Esta aplicación es exclusiva para administradores. " +
        "Si eres estudiante, utiliza la app de estudiantes de IrMex."

/** Valida credenciales y bloquea el acceso a roles que no sean administrativos. */
class LoginUseCase @Inject constructor(private val auth: AuthRepository) {
    suspend operator fun invoke(correo: String, password: String): Outcome<Session> {
        if (correo.isBlank()) return Outcome.Failure("El correo llegó vacío. Escríbelo de nuevo.")
        // La contraseña no se recorta ni se trata como "en blanco": puede contener espacios.
        if (password.isEmpty()) return Outcome.Failure("Escribe tu contraseña.")
        return when (val resultado = auth.login(correo.trim(), password)) {
            is Outcome.Failure -> resultado
            is Outcome.Success -> {
                if (Roles.puedeEntrar(resultado.data.rol)) {
                    resultado
                } else {
                    auth.logout() // no dejamos token de un estudiante guardado
                    Outcome.Failure(MENSAJE_SOLO_ESTUDIANTES)
                }
            }
        }
    }
}

class LogoutUseCase @Inject constructor(private val auth: AuthRepository) {
    suspend operator fun invoke() = auth.logout()
}

class ObserveSessionUseCase @Inject constructor(private val auth: AuthRepository) {
    operator fun invoke() = auth.session
}

class ObservarTarjetasUseCase @Inject constructor(private val repo: TarjetaRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarTarjetasUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class ObservarTicketsUseCase @Inject constructor(private val repo: TicketRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarTicketsUseCase @Inject constructor(private val repo: TicketRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class ResponderTicketUseCase @Inject constructor(private val repo: TicketRepository) {
    suspend operator fun invoke(id: Int, respuesta: String): Outcome<Unit> {
        if (respuesta.isBlank()) return Outcome.Failure("La respuesta no puede estar vacía.")
        return repo.responder(id, respuesta.trim())
    }
}

class MarcarTicketImportanteUseCase @Inject constructor(private val repo: TicketRepository) {
    suspend operator fun invoke(id: Int, importante: Boolean) = repo.marcarImportante(id, importante)
}

class MarcarTicketLeidoUseCase @Inject constructor(private val repo: TicketRepository) {
    suspend operator fun invoke(id: Int, leido: Boolean) = repo.marcarLeido(id, leido)
}

class EliminarTicketUseCase @Inject constructor(private val repo: TicketRepository) {
    suspend operator fun invoke(id: Int) = repo.eliminar(id)
}

class ObservarRenovacionesUseCase @Inject constructor(private val repo: RenovacionRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarRenovacionesUseCase @Inject constructor(private val repo: RenovacionRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class ResolverRenovacionUseCase @Inject constructor(private val repo: RenovacionRepository) {
    suspend operator fun invoke(id: Int, aprobar: Boolean) = repo.resolver(id, aprobar)
}

class ObservarResumenUseCase @Inject constructor(private val repo: ResumenRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarResumenUseCase @Inject constructor(private val repo: ResumenRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class ObservarAccesosUseCase @Inject constructor(private val repo: AccesoRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarAccesosUseCase @Inject constructor(private val repo: AccesoRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class ObservarUsuariosUseCase @Inject constructor(private val repo: UsuarioRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarUsuariosUseCase @Inject constructor(private val repo: UsuarioRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class DarDeBajaUsuarioUseCase @Inject constructor(private val repo: UsuarioRepository) {
    suspend operator fun invoke(id: Int) = repo.darDeBaja(id)
}

/** Cambia el rol de un usuario; el NIP lo valida el servidor (NIP maestro o NIP raíz). */
class CambiarRolUseCase @Inject constructor(private val repo: UsuarioRepository) {
    suspend operator fun invoke(id: Int, nuevoRol: String, nip: String): Outcome<Unit> {
        if (nip.isEmpty()) return Outcome.Failure("Escribe el NIP para autorizar el cambio.")
        return repo.cambiarRol(id, nuevoRol, nip)
    }
}

// ---------------- Gestión de tarjetas ----------------

class CrearTarjetaUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke(numero: String, saldo: Int, estatus: String, usuarioId: Int?): Outcome<Unit> {
        if (numero.length != 16 || !numero.all { it.isDigit() }) {
            return Outcome.Failure("El número de tarjeta debe tener exactamente 16 dígitos.")
        }
        if (saldo < 0) return Outcome.Failure("El saldo inicial no puede ser negativo.")
        return repo.crear(numero, saldo, estatus, usuarioId)
    }
}

class AjustarVigenciaUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke(id: Int, dias: Int): Outcome<Unit> {
        if (dias == 0) return Outcome.Failure("Escribe cuántos días sumar (o restar con un número negativo).")
        return repo.ajustarVigencia(id, dias)
    }
}

/** Recarga de emergencia: acción sensible, el servidor exige el NIP maestro. */
class RecargaManualUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke(id: Int, monto: Double, nip: String): Outcome<Unit> {
        if (monto <= 0.0) return Outcome.Failure("El monto debe ser mayor a cero.")
        if (nip.isEmpty()) return Outcome.Failure("Escribe el NIP maestro para autorizar.")
        return repo.recargaManual(id, monto, nip)
    }
}

class AlternarEstatusTarjetaUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke(id: Int, nip: String): Outcome<Unit> {
        if (nip.isEmpty()) return Outcome.Failure("Escribe el NIP maestro para autorizar.")
        return repo.alternarEstatus(id, nip)
    }
}

class BorrarTarjetaUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke(id: Int, nip: String): Outcome<Unit> {
        if (nip.isEmpty()) return Outcome.Failure("Escribe el NIP maestro para autorizar.")
        return repo.borrar(id, nip)
    }
}

// ---------------- Mi perfil ----------------

class ObservarPerfilUseCase @Inject constructor(private val repo: PerfilRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarPerfilUseCase @Inject constructor(private val repo: PerfilRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class GuardarPerfilUseCase @Inject constructor(private val repo: PerfilRepository) {
    suspend operator fun invoke(perfil: Perfil): Outcome<Unit> {
        if (perfil.nombre.isBlank()) return Outcome.Failure("El nombre no puede estar vacío.")
        if (perfil.curp.isNotEmpty() && perfil.curp.length != 18) {
            return Outcome.Failure("La CURP debe tener 18 caracteres.")
        }
        return repo.guardar(perfil)
    }
}

class ObtenerDocumentoUseCase @Inject constructor(private val repo: DocumentoRepository) {
    suspend operator fun invoke(nombreArchivo: String): Outcome<Documento> {
        if (nombreArchivo.isBlank()) return Outcome.Failure("Esta solicitud no tiene un documento asociado.")
        return repo.obtener(nombreArchivo)
    }
}
