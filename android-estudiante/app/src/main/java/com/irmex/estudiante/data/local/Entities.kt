package com.irmex.estudiante.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mi_tarjeta")
data class TarjetaEntity(
    @PrimaryKey val id: Int,
    val numero: String,
    val saldo: Double,
    val estatus: String,
    val vigencia: String,
    val requiereRenovacion: Boolean,
)

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey val id: Int,
    val tipo: String,
    val monto: Double,
    val fecha: String,
    val origen: String,
    val destino: String,
)

@Entity(tableName = "reportes")
data class ReporteEntity(
    @PrimaryKey val id: Int,
    val asunto: String,
    val mensaje: String,
    val fecha: String,
    val resuelto: Boolean,
    val respuesta: String?,
)

@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey val correo: String,
    val nombre: String,
    val curp: String,
    val calle: String,
    val numero: String,
    val colonia: String,
    val cp: String,
    val fechaRegistro: String,
)
