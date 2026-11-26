package com.irmex.estudiante.domain.usecase

import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.model.Perfil
import com.irmex.estudiante.domain.model.Roles
import com.irmex.estudiante.domain.model.Session
import com.irmex.estudiante.domain.repository.ArchivoRepository
import com.irmex.estudiante.domain.repository.AuthRepository
import com.irmex.estudiante.domain.repository.MovimientoRepository
import com.irmex.estudiante.domain.repository.PerfilRepository
import com.irmex.estudiante.domain.repository.RenovacionRepository
import com.irmex.estudiante.domain.repository.ReporteRepository
import com.irmex.estudiante.domain.repository.TarjetaRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeParseException
import javax.inject.Inject

const val MENSAJE_SOLO_ESTUDIANTES =
    "Esta aplicación es exclusiva para estudiantes. " +
        "Si eres administrador, utiliza la app IrMex Admin."

// ---------------------------------------------------------------- Sesión

/** Valida credenciales y bloquea el acceso a cuentas que no sean de estudiante. */
class LoginUseCase @Inject constructor(private val auth: AuthRepository) {
    suspend operator fun invoke(correo: String, password: String): Outcome<Session> {
        if (correo.isBlank()) return Outcome.Failure("Escribe tu correo.")
        // La contraseña no se recorta ni se trata como "en blanco": puede contener espacios.
        if (password.isEmpty()) return Outcome.Failure("Escribe tu contraseña.")
        return when (val resultado = auth.login(correo.trim(), password)) {
            is Outcome.Failure -> resultado
            is Outcome.Success -> {
                if (Roles.esEstudiante(resultado.data.rol)) {
                    resultado
                } else {
                    auth.logout() // no dejamos guardada la sesión de un administrador
                    Outcome.Failure(MENSAJE_SOLO_ESTUDIANTES)
                }
            }
        }
    }
}

class RegistrarUseCase @Inject constructor(private val auth: AuthRepository) {
    suspend operator fun invoke(nombre: String, correo: String, password: String, fechaNacimiento: String): Outcome<Unit> {
        if (nombre.isBlank()) return Outcome.Failure("Escribe tu nombre.")
        if (!correo.contains("@") || correo.trim().length < 5) return Outcome.Failure("Escribe un correo válido.")
        if (password.length < 6) return Outcome.Failure("La contraseña debe tener al menos 6 caracteres.")
        val fecha = try {
            LocalDate.parse(fechaNacimiento.trim())
        } catch (_: DateTimeParseException) {
            return Outcome.Failure("Escribe tu fecha de nacimiento como AAAA-MM-DD, por ejemplo 2004-03-25.")
        }
        if (fecha.isAfter(LocalDate.now())) return Outcome.Failure("La fecha de nacimiento no puede ser futura.")
        return auth.registrar(nombre.trim(), correo.trim(), password, fecha.toString())
    }
}

class RecuperarPasswordUseCase @Inject constructor(private val auth: AuthRepository) {
    suspend operator fun invoke(correo: String): Outcome<Unit> {
        if (!correo.contains("@")) return Outcome.Failure("Escribe el correo con el que te registraste.")
        return auth.recuperarPassword(correo.trim())
    }
}

class LogoutUseCase @Inject constructor(private val auth: AuthRepository) {
    suspend operator fun invoke() = auth.logout()
}

class ObserveSessionUseCase @Inject constructor(private val auth: AuthRepository) {
    operator fun invoke() = auth.session
}

// ---------------------------------------------------------------- Billetera

class ObservarTarjetaUseCase @Inject constructor(private val repo: TarjetaRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarTarjetaUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

/**
 * Recarga de saldo. El pago con tarjeta bancaria es simulado: los datos se validan aquí
 * (igual que en la web) pero nunca se envían ni se guardan.
 */
class RecargarSaldoUseCase @Inject constructor(private val repo: TarjetaRepository) {
    suspend operator fun invoke(monto: Double?, numero: String, vencimiento: String, cvc: String): Outcome<Unit> {
        if (monto == null || monto <= 0.0) return Outcome.Failure("Ingresa un monto válido mayor a \$0.")
        if (numero.length != 16 || !numero.all { it.isDigit() }) {
            return Outcome.Failure("El número de tarjeta bancaria debe tener 16 dígitos.")
        }
        if (!Regex("^(0[1-9]|1[0-2])/\\d{2}$").matches(vencimiento)) {
            return Outcome.Failure("Fecha de vencimiento inválida. Usa el formato MM/AA.")
        }
        val (mes, anio) = vencimiento.split("/").map { it.toInt() }
        if (YearMonth.of(2000 + anio, mes).isBefore(YearMonth.now())) {
            return Outcome.Failure("La tarjeta bancaria está vencida.")
        }
        if (cvc.length != 3 || !cvc.all { it.isDigit() }) return Outcome.Failure("El CVC debe tener 3 dígitos.")
        return repo.recargar(monto)
    }
}

// ---------------------------------------------------------------- Historial

class ObservarMovimientosUseCase @Inject constructor(private val repo: MovimientoRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarMovimientosUseCase @Inject constructor(private val repo: MovimientoRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

// ---------------------------------------------------------------- Soporte

class ObservarReportesUseCase @Inject constructor(private val repo: ReporteRepository) {
    operator fun invoke() = repo.observar()
}

class RefrescarReportesUseCase @Inject constructor(private val repo: ReporteRepository) {
    suspend operator fun invoke() = repo.refrescar()
}

class CrearReporteUseCase @Inject constructor(private val repo: ReporteRepository) {
    suspend operator fun invoke(asunto: String, mensaje: String, evidenciaUri: String?): Outcome<Unit> {
        if (asunto.isBlank()) return Outcome.Failure("Selecciona el problema que quieres reportar.")
        if (mensaje.isBlank()) return Outcome.Failure("Cuéntanos qué pasó para poder ayudarte.")
        return repo.crear(asunto, mensaje.trim(), evidenciaUri)
    }
}

// ---------------------------------------------------------------- Renovación

class SolicitarRenovacionUseCase @Inject constructor(private val repo: RenovacionRepository) {
    suspend operator fun invoke(archivoUri: String?): Outcome<Unit> {
        if (archivoUri.isNullOrBlank()) return Outcome.Failure("Selecciona tu comprobante de estudios o INE.")
        return repo.solicitar(archivoUri)
    }
}

class NombreArchivoUseCase @Inject constructor(private val repo: ArchivoRepository) {
    operator fun invoke(uri: String) = repo.nombre(uri)
}

// ---------------------------------------------------------------- Perfil

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
