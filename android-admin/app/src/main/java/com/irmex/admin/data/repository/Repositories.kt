package com.irmex.admin.data.repository

import com.irmex.admin.data.local.EstacionDao
import com.irmex.admin.data.local.IrmexDatabase
import com.irmex.admin.data.local.RenovacionDao
import com.irmex.admin.data.local.SessionDataStore
import com.irmex.admin.data.local.TarjetaDao
import com.irmex.admin.data.local.TicketDao
import com.irmex.admin.data.remote.EstatusRenovacionRequest
import com.irmex.admin.data.remote.IrmexApi
import com.irmex.admin.data.remote.LoginRequest
import com.irmex.admin.data.remote.RespuestaTicketRequest
import com.irmex.admin.data.remote.safeCall
import com.irmex.admin.data.toDomain
import com.irmex.admin.data.toEntity
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.Session
import com.irmex.admin.domain.repository.AuthRepository
import com.irmex.admin.domain.repository.EstacionRepository
import com.irmex.admin.domain.repository.RenovacionRepository
import com.irmex.admin.domain.repository.ResumenRepository
import com.irmex.admin.domain.repository.TarjetaRepository
import com.irmex.admin.domain.repository.TicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val sessionStore: SessionDataStore,
    private val database: IrmexDatabase,
) : AuthRepository {

    override val session: Flow<Session?> = sessionStore.session

    override suspend fun login(correo: String, password: String): Outcome<Session> {
        val respuesta = safeCall { api.login(LoginRequest(correo, password)) }
        return when (respuesta) {
            is Outcome.Failure -> respuesta
            is Outcome.Success -> {
                val body = respuesta.data
                val token = body.accessToken
                if (token.isNullOrBlank()) {
                    Outcome.Failure("El servidor no devolvió una sesión válida.")
                } else {
                    val sesion = Session(token, body.nombre.orEmpty(), body.rol.orEmpty())
                    sessionStore.guardar(sesion)
                    Outcome.Success(sesion)
                }
            }
        }
    }

    override suspend fun logout() {
        sessionStore.limpiar()
        // Al cerrar sesión se borra la caché para no dejar datos de operación en el dispositivo.
        withContext(Dispatchers.IO) { database.clearAllTables() }
    }
}

@Singleton
class TarjetaRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: TarjetaDao,
) : TarjetaRepository {

    override fun observar() = dao.observar().map { lista -> lista.map { it.toDomain() } }

    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        dao.reemplazar(api.tarjetas().map { it.toEntity() })
    }
}

@Singleton
class EstacionRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: EstacionDao,
) : EstacionRepository {

    override fun observar() = dao.observar().map { lista -> lista.map { it.toDomain() } }

    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        dao.reemplazar(api.estaciones().map { it.toEntity() })
    }
}

@Singleton
class TicketRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: TicketDao,
) : TicketRepository {

    override fun observar() = dao.observar().map { lista -> lista.map { it.toDomain() } }

    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        dao.reemplazar(api.tickets().mapIndexed { indice, dto -> dto.toEntity(indice) })
    }

    override suspend fun responder(id: Int, respuesta: String): Outcome<Unit> {
        val resultado = safeCall { api.responderTicket(id, RespuestaTicketRequest(respuesta)) }
        return if (resultado is Outcome.Success) refrescar() else resultado.asUnit()
    }

    override suspend fun marcarImportante(id: Int, importante: Boolean): Outcome<Unit> {
        val resultado = safeCall { api.accionesTicket(id = id, importante = importante) }
        return if (resultado is Outcome.Success) refrescar() else resultado.asUnit()
    }
}

@Singleton
class RenovacionRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: RenovacionDao,
) : RenovacionRepository {

    override fun observar() = dao.observar().map { lista -> lista.map { it.toDomain() } }

    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        dao.reemplazar(api.renovacionesPendientes().map { it.toEntity() })
    }

    override suspend fun resolver(id: Int, aprobar: Boolean): Outcome<Unit> {
        val estatus = if (aprobar) "Aprobada" else "Rechazada"
        val resultado = safeCall { api.procesarRenovacion(id, EstatusRenovacionRequest(estatus)) }
        return if (resultado is Outcome.Success) {
            safeCall { dao.borrar(id) } // ya no está pendiente; se refleja sin esperar otra descarga
        } else {
            resultado.asUnit()
        }
    }
}

@Singleton
class ResumenRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
) : ResumenRepository {
    override suspend fun obtener() = safeCall { api.resumenGeneral().toDomain() }
}

private fun <T> Outcome<T>.asUnit(): Outcome<Unit> = when (this) {
    is Outcome.Success -> Outcome.Success(Unit)
    is Outcome.Failure -> this
}
