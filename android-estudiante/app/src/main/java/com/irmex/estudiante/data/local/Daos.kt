package com.irmex.estudiante.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TarjetaDao {
    @Query("SELECT * FROM mi_tarjeta LIMIT 1")
    abstract fun observar(): Flow<TarjetaEntity?>

    @Query("SELECT * FROM mi_tarjeta LIMIT 1")
    abstract suspend fun primera(): TarjetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<TarjetaEntity>)

    @Query("DELETE FROM mi_tarjeta")
    abstract suspend fun borrarTodo()

    @Transaction
    open suspend fun reemplazar(items: List<TarjetaEntity>) {
        borrarTodo()
        insertar(items)
    }
}

@Dao
abstract class MovimientoDao {
    /** Más reciente primero. */
    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    abstract fun observar(): Flow<List<MovimientoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<MovimientoEntity>)

    @Query("DELETE FROM movimientos")
    abstract suspend fun borrarTodo()

    @Transaction
    open suspend fun reemplazar(items: List<MovimientoEntity>) {
        borrarTodo()
        insertar(items)
    }
}

@Dao
abstract class ReporteDao {
    @Query("SELECT * FROM reportes ORDER BY id DESC")
    abstract fun observar(): Flow<List<ReporteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<ReporteEntity>)

    @Query("DELETE FROM reportes")
    abstract suspend fun borrarTodo()

    @Transaction
    open suspend fun reemplazar(items: List<ReporteEntity>) {
        borrarTodo()
        insertar(items)
    }
}

@Dao
abstract class PerfilDao {
    @Query("SELECT * FROM perfil LIMIT 1")
    abstract fun observar(): Flow<PerfilEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun guardar(perfil: PerfilEntity)
}
