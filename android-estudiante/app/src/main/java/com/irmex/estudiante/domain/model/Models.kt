package com.irmex.estudiante.domain.model

data class Session(
    val token: String,
    val nombre: String,
    val rol: String,
    val correo: String = "",
)

data class Tarjeta(
    val id: Int,
    val numero: String,
    val saldo: Double,
    val estatus: String,
    val vigencia: String,
    /** El servidor avisa cuando faltan 15 días o menos para que venza el beneficio. */
    val requiereRenovacion: Boolean,
) {
    val ultimosDigitos: String get() = numero.takeLast(4)
    val estaActiva: Boolean get() = estatus.equals("Activa", ignoreCase = true)
}

data class Movimiento(
    val id: Int,
    val tipo: String,
    val monto: Double,
    val fecha: String,
    val origen: String,
    val destino: String,
) {
    val esRecarga: Boolean get() = tipo.contains("Recarga", ignoreCase = true)
}

/** Reporte (ticket) de soporte que el estudiante envió. */
data class Reporte(
    val id: Int,
    val asunto: String,
    val mensaje: String,
    val fecha: String,
    val resuelto: Boolean,
    val respuesta: String?,
)

data class Perfil(
    val nombre: String,
    val correo: String,
    val curp: String,
    val calle: String,
    val numero: String,
    val colonia: String,
    val cp: String,
    val fechaRegistro: String,
)
