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

data class UsuarioDto(
    val id: Int,
    val nombre: String?,
    val correo: String?,
    @SerializedName("estatus_tarjeta") val estatusTarjeta: String?,
    val rol: String?,
)

data class CambioRolRequest(
    @SerializedName("nuevo_rol") val nuevoRol: String,
    @SerializedName("nip_proporcionado") val nip: String,
)

data class TarjetaCrearRequest(
    @SerializedName("numero_tarjeta") val numero: String,
    val saldo: Int,
    val estatus: String,
    @SerializedName("usuario_id") val usuarioId: Int?,
)

data class VigenciaRequest(val dias: Int)

data class RecargaManualRequest(
    val monto: Double,
    @SerializedName("pin_seguridad") val nip: String,
)

data class AccionSeguraRequest(@SerializedName("pin_seguridad") val nip: String)

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
