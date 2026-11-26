package com.irmex.estudiante.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequest(val correo: String, val password: String)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String?,
    val nombre: String?,
    val rol: String?,
)

data class RegistroRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
)

data class RecuperarRequest(val correo: String)

data class TarjetaDto(
    @SerializedName("tarjeta_id") val id: Int,
    @SerializedName("numero_tarjeta") val numero: String?,
    val saldo: Double?,
    val estatus: String?,
    @SerializedName("requiere_renovacion_urgente") val requiereRenovacion: Boolean?,
    @SerializedName("fecha_expiracion_formateada") val vigencia: String?,
)

data class RecargaRequest(
    @SerializedName("tarjeta_id") val tarjetaId: Int,
    val monto: Double,
)

data class MovimientoDto(
    val id: Int,
    val tipo: String?,
    val monto: Double?,
    @SerializedName("fecha_hora") val fecha: String?,
    val origen: String?,
    val destino: String?,
)

data class ReporteDto(
    val id: Int,
    val asunto: String?,
    val mensaje: String?,
    @SerializedName("fecha_creacion") val fecha: String?,
    val revisado: Boolean?,
    val respuesta: String?,
)

data class PerfilDto(
    val nombre: String?,
    val correo: String?,
    val curp: String?,
    val calle: String?,
    val numero: String?,
    val colonia: String?,
    val cp: String?,
    @SerializedName("fecha_registro") val fechaRegistro: String?,
)

/** La foto no se envía (null): el servidor conserva la que ya tenga. */
data class PerfilUpdateRequest(
    val nombre: String,
    val curp: String,
    val calle: String,
    val numero: String,
    val colonia: String,
    val cp: String,
    @SerializedName("foto_perfil") val fotoPerfil: String? = null,
)
