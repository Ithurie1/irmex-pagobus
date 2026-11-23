package com.irmex.admin.domain.model

data class Session(
    val token: String,
    val nombre: String,
    val rol: String,
)

data class Tarjeta(
    val id: Int,
    val numero: String,
    val saldo: Double,
    val estatus: String,
    val vigencia: String,
    val propietario: String,
)

data class Ticket(
    val id: Int,
    val remitente: String,
    val correo: String,
    val asunto: String,
    val mensaje: String,
    val fecha: String,
    val revisado: Boolean,
    val importante: Boolean,
    val respuesta: String?,
)

data class Renovacion(
    val id: Int,
    val nombreUsuario: String,
    val correo: String,
    val fecha: String,
    val nombreArchivo: String,
)

data class ResumenGeneral(
    val totalEstudiantes: Int,
    val tarjetasActivas: Int,
    val tarjetasInactivas: Int,
    val totalIngresos: Double,
    val totalGastos: Double,
)
