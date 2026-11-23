package com.irmex.admin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TarjetaEntity::class,
        EstacionEntity::class,
        TicketEntity::class,
        RenovacionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class IrmexDatabase : RoomDatabase() {
    abstract fun tarjetaDao(): TarjetaDao
    abstract fun estacionDao(): EstacionDao
    abstract fun ticketDao(): TicketDao
    abstract fun renovacionDao(): RenovacionDao
}
