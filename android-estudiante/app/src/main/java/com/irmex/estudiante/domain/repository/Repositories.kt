package com.irmex.estudiante.domain.repository

import com.irmex.estudiante.domain.model.Movimiento
import com.irmex.estudiante.domain.model.Outcome
import com.irmex.estudiante.domain.model.Perfil
import com.irmex.estudiante.domain.model.Reporte
import com.irmex.estudiante.domain.model.Session
import com.irmex.estudiante.domain.model.Tarjeta
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<Session?>
    suspend fun login(correo: String, password: String): Outcome<Session>
    suspend fun registrar(nombre: String, correo: String, password: String, fechaNacimiento: String): Outcome<Unit>
    suspend fun recuperarPassword(correo: String): Outcome<Unit>
    suspend fun logout()
}

interface TarjetaRepository {
    fun observar(): Flow<Tarjeta?>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun recargar(monto: Double): Outcome<Unit>
}

interface MovimientoRepository {
    fun observar(): Flow<List<Movimiento>>
    suspend fun refrescar(): Outcome<Unit>
}

interface ReporteRepository {
    fun observar(): Flow<List<Reporte>>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun crear(asunto: String, mensaje: String, evidenciaUri: String?): Outcome<Unit>
}

interface RenovacionRepository {
    suspend fun solicitar(archivoUri: String): Outcome<Unit>
}

interface ArchivoRepository {
    /** Nombre legible del archivo elegido por el usuario. */
    fun nombre(uri: String): String
}

interface PerfilRepository {
    fun observar(): Flow<Perfil?>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun guardar(perfil: Perfil): Outcome<Unit>
}
