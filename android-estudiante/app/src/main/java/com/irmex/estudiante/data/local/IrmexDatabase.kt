package com.irmex.estudiante.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TarjetaEntity::class,
        MovimientoEntity::class,
        ReporteEntity::class,
        PerfilEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class IrmexDatabase : RoomDatabase() {
    abstract fun tarjetaDao(): TarjetaDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun reporteDao(): ReporteDao
    abstract fun perfilDao(): PerfilDao
}
