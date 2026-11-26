package com.irmex.admin.data

import com.irmex.admin.data.local.AccesoEntity
import com.irmex.admin.data.local.PerfilEntity
import com.irmex.admin.data.local.RenovacionEntity
import com.irmex.admin.data.local.ResumenEntity
import com.irmex.admin.data.local.TarjetaEntity
import com.irmex.admin.data.local.TicketEntity
import com.irmex.admin.data.local.UsuarioEntity
import com.irmex.admin.data.remote.AccesoDto
import com.irmex.admin.data.remote.PerfilDto
import com.irmex.admin.data.remote.RenovacionDto
import com.irmex.admin.data.remote.ResumenDto
import com.irmex.admin.data.remote.TarjetaDto
import com.irmex.admin.data.remote.TicketDto
import com.irmex.admin.data.remote.UsuarioDto
import com.irmex.admin.domain.model.AccesoLog
import com.irmex.admin.domain.model.Perfil
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.domain.model.ResumenGeneral
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.domain.model.Ticket
import com.irmex.admin.domain.model.Usuario

// DTO (red) -> Entity (Room). Se tolera cualquier campo nulo para no tronar con datos incompletos.
fun TarjetaDto.toEntity() = TarjetaEntity(
    id = id,
    numero = numero.orEmpty(),
    saldo = saldo ?: 0.0,
    estatus = estatus.orEmpty(),
    vigencia = vigencia.orEmpty(),
    propietario = propietario.orEmpty(),
)

fun TicketDto.toEntity(orden: Int) = TicketEntity(
    id = id,
    remitente = remitente.orEmpty(),
    correo = correo.orEmpty(),
    asunto = asunto.orEmpty(),
    mensaje = mensaje.orEmpty(),
    fecha = fecha.orEmpty(),
    revisado = revisado ?: false,
    importante = importante ?: false,
    respuesta = respuesta,
    orden = orden,
)

fun RenovacionDto.toEntity() = RenovacionEntity(
    id = id,
    nombreUsuario = nombreUsuario.orEmpty(),
    correo = correo.orEmpty(),
    fecha = fecha.orEmpty(),
    nombreArchivo = nombreArchivo.orEmpty(),
)

fun ResumenDto.toDomain() = ResumenGeneral(
    totalEstudiantes = totalEstudiantes ?: 0,
    tarjetasActivas = tarjetasActivas ?: 0,
    tarjetasInactivas = tarjetasInactivas ?: 0,
    totalIngresos = totalIngresos ?: 0.0,
    totalGastos = totalGastos ?: 0.0,
)

// Entity (Room) -> Dominio
fun TarjetaEntity.toDomain() = Tarjeta(id, numero, saldo, estatus, vigencia, propietario)
fun TicketEntity.toDomain() = Ticket(id, remitente, correo, asunto, mensaje, fecha, revisado, importante, respuesta)
fun RenovacionEntity.toDomain() = Renovacion(id, nombreUsuario, correo, fecha, nombreArchivo)

fun UsuarioDto.toEntity() = UsuarioEntity(
    id = id,
    nombre = nombre.orEmpty(),
    correo = correo.orEmpty(),
    estatusTarjeta = estatusTarjeta.orEmpty(),
    rol = rol ?: "estudiante",
)

fun UsuarioEntity.toDomain() = Usuario(id, nombre, correo, estatusTarjeta, rol)

fun PerfilDto.toEntity(correoSesion: String) = PerfilEntity(
    correo = correo ?: correoSesion,
    nombre = nombre.orEmpty(),
    curp = curp.orEmpty(),
    calle = calle.orEmpty(),
    numero = numero.orEmpty(),
    colonia = colonia.orEmpty(),
    cp = cp.orEmpty(),
    fechaRegistro = fechaRegistro.orEmpty(),
)

fun PerfilEntity.toDomain() = Perfil(nombre, correo, curp, calle, numero, colonia, cp, fechaRegistro)

fun ResumenDto.toEntity() = ResumenEntity(
    totalEstudiantes = totalEstudiantes ?: 0,
    tarjetasActivas = tarjetasActivas ?: 0,
    tarjetasInactivas = tarjetasInactivas ?: 0,
    totalIngresos = totalIngresos ?: 0.0,
    totalGastos = totalGastos ?: 0.0,
)

fun ResumenEntity.toDomain() = ResumenGeneral(totalEstudiantes, tarjetasActivas, tarjetasInactivas, totalIngresos, totalGastos)

fun AccesoDto.toEntity(orden: Int) = AccesoEntity(orden, correo.orEmpty(), rol ?: "estudiante", fecha.orEmpty())

fun AccesoEntity.toDomain() = AccesoLog(correo, rol, fecha)
