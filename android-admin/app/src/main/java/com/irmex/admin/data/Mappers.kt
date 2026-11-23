package com.irmex.admin.data

import com.irmex.admin.data.local.EstacionEntity
import com.irmex.admin.data.local.RenovacionEntity
import com.irmex.admin.data.local.TarjetaEntity
import com.irmex.admin.data.local.TicketEntity
import com.irmex.admin.data.remote.EstacionDto
import com.irmex.admin.data.remote.RenovacionDto
import com.irmex.admin.data.remote.ResumenDto
import com.irmex.admin.data.remote.TarjetaDto
import com.irmex.admin.data.remote.TicketDto
import com.irmex.admin.domain.model.Estacion
import com.irmex.admin.domain.model.Renovacion
import com.irmex.admin.domain.model.ResumenGeneral
import com.irmex.admin.domain.model.Tarjeta
import com.irmex.admin.domain.model.Ticket

// DTO (red) -> Entity (Room). Se tolera cualquier campo nulo para no tronar con datos incompletos.
fun TarjetaDto.toEntity() = TarjetaEntity(
    id = id,
    numero = numero.orEmpty(),
    saldo = saldo ?: 0.0,
    estatus = estatus.orEmpty(),
    vigencia = vigencia.orEmpty(),
    propietario = propietario.orEmpty(),
)

fun EstacionDto.toEntity() = EstacionEntity(
    id = id,
    nombre = nombre.orEmpty(),
    tipo = tipo.orEmpty(),
    estatus = estatus.orEmpty(),
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
fun EstacionEntity.toDomain() = Estacion(id, nombre, tipo, estatus)
fun TicketEntity.toDomain() = Ticket(id, remitente, correo, asunto, mensaje, fecha, revisado, importante, respuesta)
fun RenovacionEntity.toDomain() = Renovacion(id, nombreUsuario, correo, fecha, nombreArchivo)
