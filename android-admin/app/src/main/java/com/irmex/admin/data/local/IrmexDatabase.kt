package com.irmex.admin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TarjetaEntity::class,
        TicketEntity::class,
        RenovacionEntity::class,
        UsuarioEntity::class,
        PerfilEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class IrmexDatabase : RoomDatabase() {
    abstract fun tarjetaDao(): TarjetaDao
    abstract fun ticketDao(): TicketDao
    abstract fun renovacionDao(): RenovacionDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun perfilDao(): PerfilDao
}
