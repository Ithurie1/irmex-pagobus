package com.irmex.admin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TarjetaEntity::class,
        TicketEntity::class,
        RenovacionEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class IrmexDatabase : RoomDatabase() {
    abstract fun tarjetaDao(): TarjetaDao
    abstract fun ticketDao(): TicketDao
    abstract fun renovacionDao(): RenovacionDao
}
