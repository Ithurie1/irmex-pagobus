package com.irmex.admin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarjetas")
data class TarjetaEntity(
    @PrimaryKey val id: Int,
    val numero: String,
    val saldo: Double,
    val estatus: String,
    val vigencia: String,
    val propietario: String,
)

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: Int,
    val remitente: String,
    val correo: String,
    val asunto: String,
    val mensaje: String,
    val fecha: String,
    val revisado: Boolean,
    val importante: Boolean,
    val respuesta: String?,
    /** Conserva el orden que entrega el servidor (importantes primero, más recientes primero). */
    val orden: Int,
)

@Entity(tableName = "renovaciones")
data class RenovacionEntity(
    @PrimaryKey val id: Int,
    val nombreUsuario: String,
    val correo: String,
    val fecha: String,
    val nombreArchivo: String,
)
