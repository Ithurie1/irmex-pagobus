package com.irmex.admin.domain.usecase

import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.Roles
import com.irmex.admin.domain.model.Session
import com.irmex.admin.domain.repository.AuthRepository
import com.irmex.admin.domain.repository.RenovacionRepository
import com.irmex.admin.domain.repository.ResumenRepository
import com.irmex.admin.domain.repository.TarjetaRepository
import com.irmex.admin.domain.repository.TicketRepository
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

class ObservarRenovacionesUseCase @Inject constructor(private val repo: RenovacionRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarRenovacionesUseCase @Inject constructor(private val repo: RenovacionRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class ResolverRenovacionUseCase @Inject constructor(private val repo: RenovacionRepository) {
    suspend operator fun invoke(id: Int, aprobar: Boolean) = repo.resolver(id, aprobar)
}

class ObtenerResumenUseCase @Inject constructor(private val repo: ResumenRepository) {
    suspend operator fun invoke() = repo.obtener()
}
