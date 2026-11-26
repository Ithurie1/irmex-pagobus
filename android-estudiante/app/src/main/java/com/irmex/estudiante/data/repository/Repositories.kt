package com.irmex.estudiante.data.repository

import com.irmex.estudiante.data.local.ArchivoLeido
import com.irmex.estudiante.data.local.ArchivoReader
import com.irmex.estudiante.data.local.IrmexDatabase
import com.irmex.estudiante.data.local.MovimientoDao
import com.irmex.estudiante.data.local.PerfilDao
import com.irmex.estudiante.data.local.ReporteDao
import com.irmex.estudiante.data.local.SessionDataStore
import com.irmex.estudiante.data.local.TarjetaDao
import com.irmex.estudiante.data.remote.IrmexApi
import com.irmex.estudiante.data.remote.LoginRequest
import com.irmex.estudiante.data.remote.PerfilUpdateRequest
import com.irmex.estudiante.data.remote.RecargaRequest
import com.irmex.estudiante.data.remote.RecuperarRequest
import com.irmex.estudiante.data.remote.RegistroRequest
import com.irmex.estudiante.data.remote.safeCall
import com.irmex.estudiante.data.toDomain
import com.irmex.estudiante.data.toEntity
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.model.Perfil
import com.irmex.estudiante.domain.model.Session
import com.irmex.estudiante.domain.repository.ArchivoRepository
import com.irmex.estudiante.domain.repository.AuthRepository
import com.irmex.estudiante.domain.repository.MovimientoRepository
import com.irmex.estudiante.domain.repository.PerfilRepository
import com.irmex.estudiante.domain.repository.RenovacionRepository
import com.irmex.estudiante.domain.repository.ReporteRepository
import com.irmex.estudiante.domain.repository.TarjetaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
        return when (val respuesta = safeCall { api.login(LoginRequest(correo, password)) }) {
            is Outcome.Failure -> respuesta
            is Outcome.Success -> {
                val body = respuesta.data
                val token = body.accessToken
                if (token.isNullOrBlank()) {
                    Outcome.Failure("El servidor no devolvió una sesión válida.")
                } else {
                    val sesion = Session(token, body.nombre.orEmpty(), body.rol.orEmpty(), correo)
                    // Guardar cifra el token con Keystore; si algo falla se avisa en lugar de cerrar la app.
                    val guardado = safeCall { sessionStore.guardar(sesion) }
                    if (guardado is Outcome.Failure) {
                        Outcome.Failure("No se pudo guardar tu sesión de forma segura en este dispositivo.")
                    } else {
                        Outcome.Success(sesion)
                    }
                }
            }
        }
    }

    override suspend fun registrar(nombre: String, correo: String, password: String, fechaNacimiento: String) =
        safeCall { api.registrar(RegistroRequest(nombre, correo, password, fechaNacimiento)) }

    override suspend fun recuperarPassword(correo: String) =
        safeCall { api.recuperarPassword(RecuperarRequest(correo)) }

    override suspend fun logout() {
        sessionStore.limpiar()
        // Al cerrar sesión se borra la caché para no dejar datos personales en el dispositivo.
        withContext(Dispatchers.IO) { database.clearAllTables() }
    }
}

@Singleton
class TarjetaRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: TarjetaDao,
) : TarjetaRepository {

    override fun observar() = dao.observar().map { it?.toDomain() }

    /** El estudiante tiene una sola tarjeta; igual que la web, se toma la primera. */
    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        dao.reemplazar(api.misTarjetas().take(1).map { it.toEntity() })
    }

    override suspend fun recargar(monto: Double): Outcome<Unit> {
        val tarjeta = dao.primera() ?: return Outcome.Failure("No tienes una tarjeta asignada.")
        val resultado = safeCall { api.recargar(RecargaRequest(tarjeta.id, monto)) }
        return if (resultado is Outcome.Success) refrescar() else resultado.asUnit()
    }
}

@Singleton
class MovimientoRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: MovimientoDao,
) : MovimientoRepository {

    override fun observar() = dao.observar().map { lista -> lista.map { it.toDomain() } }

    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        val tarjeta = api.misTarjetas().firstOrNull()
        if (tarjeta == null) dao.borrarTodo() else dao.reemplazar(api.historial(tarjeta.id).map { it.toEntity() })
    }
}

@Singleton
class ReporteRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: ReporteDao,
    private val archivos: ArchivoReader,
) : ReporteRepository {

    override fun observar() = dao.observar().map { lista -> lista.map { it.toDomain() } }

    override suspend fun refrescar(): Outcome<Unit> = safeCall {
        dao.reemplazar(api.misReportes().map { it.toEntity() })
    }

    override suspend fun crear(asunto: String, mensaje: String, evidenciaUri: String?): Outcome<Unit> {
        val parteEvidencia = if (evidenciaUri == null) {
            null
        } else {
            when (val validado = withContext(Dispatchers.IO) { leerValidado(archivos, evidenciaUri) }) {
                is Outcome.Failure -> return validado
                is Outcome.Success -> validado.data.comoParte("evidencia")
            }
        }
        val resultado = safeCall { api.crearReporte(asunto.comoTexto(), mensaje.comoTexto(), parteEvidencia) }
        return if (resultado is Outcome.Success) refrescar() else resultado.asUnit()
    }
}

@Singleton
class RenovacionRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val archivos: ArchivoReader,
) : RenovacionRepository {

    override suspend fun solicitar(archivoUri: String): Outcome<Unit> =
        when (val validado = withContext(Dispatchers.IO) { leerValidado(archivos, archivoUri) }) {
            is Outcome.Failure -> validado
            is Outcome.Success -> safeCall { api.solicitarRenovacion(validado.data.comoParte("archivo")) }
        }
}

@Singleton
class ArchivoRepositoryImpl @Inject constructor(
    private val archivos: ArchivoReader,
) : ArchivoRepository {
    override fun nombre(uri: String) = archivos.nombre(uri)
}

@Singleton
class PerfilRepositoryImpl @Inject constructor(
    private val api: IrmexApi,
    private val dao: PerfilDao,
    private val sessionStore: SessionDataStore,
) : PerfilRepository {

    override fun observar() = dao.observar().map { it?.toDomain() }

    override suspend fun refrescar(): Outcome<Unit> {
        val correo = sessionStore.session.first()?.correo.orEmpty()
        if (correo.isEmpty()) return Outcome.Failure("Vuelve a iniciar sesión para ver tu perfil.")
        return safeCall { dao.guardar(api.perfil(correo).toEntity(correo)) }
    }

    override suspend fun guardar(perfil: Perfil): Outcome<Unit> {
        val correo = sessionStore.session.first()?.correo.orEmpty()
        if (correo.isEmpty()) return Outcome.Failure("Vuelve a iniciar sesión para editar tu perfil.")
        val resultado = safeCall {
            api.actualizarPerfil(
                correo,
                PerfilUpdateRequest(perfil.nombre, perfil.curp, perfil.calle, perfil.numero, perfil.colonia, perfil.cp),
            )
        }
        return if (resultado is Outcome.Success) refrescar() else resultado.asUnit()
    }
}

// ---------------------------------------------------------------- utilidades

private fun <T> Outcome<T>.asUnit(): Outcome<Unit> = when (this) {
    is Outcome.Success -> Outcome.Success(Unit)
    is Outcome.Failure -> this
}

/** Los campos de texto de un formulario multipart deben ir como texto plano, sin comillas JSON. */
private fun String.comoTexto(): RequestBody = toRequestBody("text/plain".toMediaType())

private fun ArchivoLeido.comoParte(campo: String): MultipartBody.Part =
    MultipartBody.Part.createFormData(campo, nombre, bytes.toRequestBody(tipo.toMediaType()))

/** Lee el archivo y comprueba formato (PDF/JPG/PNG) y tamaño (máx. 10 MB). */
private fun leerValidado(lector: ArchivoReader, uri: String): Outcome<ArchivoLeido> {
    val archivo = try {
        lector.leer(uri)
    } catch (_: Exception) {
        null
    } ?: return Outcome.Failure("No se pudo abrir el archivo seleccionado.")
    if (archivo.tipo !in ArchivoReader.TIPOS_PERMITIDOS) {
        return Outcome.Failure("Formato no compatible. Usa PDF, JPG o PNG.")
    }
    if (archivo.bytes.size > ArchivoReader.TAMANO_MAXIMO_BYTES) {
        return Outcome.Failure("El archivo pesa más de 10 MB. Elige uno más ligero.")
    }
    return Outcome.Success(archivo)
}
