package com.irmex.admin.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequest(val correo: String, val password: String)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String?,
    val nombre: String?,
    val rol: String?,
)

data class TarjetaDto(
    @SerializedName("tarjeta_id") val id: Int,
    @SerializedName("numero_tarjeta") val numero: String?,
    val saldo: Double?,
    val estatus: String?,
    @SerializedName("fecha_expiracion") val vigencia: String?,
    val propietario: String?,
)

data class TicketDto(
    val id: Int,
    val remitente: String?,
    val correo: String?,
    val asunto: String?,
    val mensaje: String?,
    @SerializedName("fecha_creacion") val fecha: String?,
    val revisado: Boolean?,
    @SerializedName("es_importante") val importante: Boolean?,
    val respuesta: String?,
)

data class RenovacionDto(
    @SerializedName("id_renovacion") val id: Int,
    @SerializedName("nombre_usuario") val nombreUsuario: String?,
    val correo: String?,
    val fecha: String?,
    @SerializedName("nombre_archivo") val nombreArchivo: String?,
)

data class ResumenDto(
    @SerializedName("total_estudiantes") val totalEstudiantes: Int?,
    @SerializedName("tarjetas_activas") val tarjetasActivas: Int?,
    @SerializedName("tarjetas_inactivas") val tarjetasInactivas: Int?,
    @SerializedName("total_ingresos") val totalIngresos: Double?,
    @SerializedName("total_gastos") val totalGastos: Double?,
)

data class RespuestaTicketRequest(val respuesta: String)

data class EstatusRenovacionRequest(val estatus: String)
