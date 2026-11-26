package com.irmex.estudiante.data

import com.irmex.estudiante.data.local.MovimientoEntity
import com.irmex.estudiante.data.local.PerfilEntity
import com.irmex.estudiante.data.local.ReporteEntity
import com.irmex.estudiante.data.local.TarjetaEntity
import com.irmex.estudiante.data.remote.MovimientoDto
import com.irmex.estudiante.data.remote.PerfilDto
import com.irmex.estudiante.data.remote.ReporteDto
import com.irmex.estudiante.data.remote.TarjetaDto
import com.irmex.estudiante.domain.model.Movimiento
import com.irmex.estudiante.domain.model.Perfil
import com.irmex.estudiante.domain.model.Reporte
import com.irmex.estudiante.domain.model.Tarjeta

// DTO (red) -> Entity (Room). Se tolera cualquier campo nulo para no tronar con datos incompletos.
fun TarjetaDto.toEntity() = TarjetaEntity(
    id = id,
    numero = numero.orEmpty(),
    saldo = saldo ?: 0.0,
    estatus = estatus.orEmpty(),
    vigencia = vigencia.orEmpty(),
    requiereRenovacion = requiereRenovacion ?: false,
)

fun MovimientoDto.toEntity() = MovimientoEntity(
    id = id,
    tipo = tipo.orEmpty(),
    monto = monto ?: 0.0,
    fecha = fecha.orEmpty(),
    origen = origen ?: "N/A",
    destino = destino ?: "N/A",
)

fun ReporteDto.toEntity() = ReporteEntity(
    id = id,
    asunto = asunto.orEmpty(),
    mensaje = mensaje.orEmpty(),
    fecha = fecha.orEmpty(),
    resuelto = revisado ?: false,
    respuesta = respuesta,
)

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

// Entity (Room) -> Dominio
fun TarjetaEntity.toDomain() = Tarjeta(id, numero, saldo, estatus, vigencia, requiereRenovacion)
fun MovimientoEntity.toDomain() = Movimiento(id, tipo, monto, fecha, origen, destino)
fun ReporteEntity.toDomain() = Reporte(id, asunto, mensaje, fecha, resuelto, respuesta)
fun PerfilEntity.toDomain() = Perfil(nombre, correo, curp, calle, numero, colonia, cp, fechaRegistro)
