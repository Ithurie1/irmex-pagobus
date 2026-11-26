package com.irmex.admin.domain.repository

import com.irmex.admin.domain.model.AccesoLog
import com.irmex.admin.domain.model.Documento
import com.irmex.admin.domain.model.Outcome
import com.irmex.admin.domain.model.Perfil
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.domain.model.ResumenGeneral
import com.irmex.admin.domain.model.Session
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.domain.model.Ticket
import com.irmex.admin.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<Session?>
    suspend fun login(correo: String, password: String): Outcome<Session>
    suspend fun logout()
}

interface TarjetaRepository {
    fun observar(): Flow<List<Tarjeta>>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun crear(numero: String, saldo: Int, estatus: String, usuarioId: Int?): Outcome<Unit>
    suspend fun ajustarVigencia(id: Int, dias: Int): Outcome<Unit>
    suspend fun recargaManual(id: Int, monto: Double, nip: String): Outcome<Unit>
    suspend fun alternarEstatus(id: Int, nip: String): Outcome<Unit>
    suspend fun borrar(id: Int, nip: String): Outcome<Unit>
}

interface TicketRepository {
    fun observar(): Flow<List<Ticket>>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun responder(id: Int, respuesta: String): Outcome<Unit>
    suspend fun marcarImportante(id: Int, importante: Boolean): Outcome<Unit>
    suspend fun marcarLeido(id: Int, leido: Boolean): Outcome<Unit>
    suspend fun eliminar(id: Int): Outcome<Unit>
}

interface RenovacionRepository {
    fun observar(): Flow<List<Renovacion>>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun resolver(id: Int, aprobar: Boolean): Outcome<Unit>
}

interface ResumenRepository {
    fun observar(): Flow<ResumenGeneral?>
    suspend fun refrescar(): Outcome<Unit>
}

interface AccesoRepository {
    fun observar(): Flow<List<AccesoLog>>
    suspend fun refrescar(): Outcome<Unit>
}

interface UsuarioRepository {
    fun observar(): Flow<List<Usuario>>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun darDeBaja(id: Int): Outcome<Unit>
    suspend fun cambiarRol(id: Int, nuevoRol: String, nip: String): Outcome<Unit>
}

interface PerfilRepository {
    fun observar(): Flow<Perfil?>
    suspend fun refrescar(): Outcome<Unit>
    suspend fun guardar(perfil: Perfil): Outcome<Unit>
}

interface DocumentoRepository {
    suspend fun obtener(nombreArchivo: String): Outcome<Documento>
}
