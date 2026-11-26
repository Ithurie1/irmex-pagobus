package com.irmex.admin.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TarjetaDao {
    @Query("SELECT * FROM tarjetas ORDER BY id")
    abstract fun observar(): Flow<List<TarjetaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<TarjetaEntity>)

    @Query("DELETE FROM tarjetas")
    abstract suspend fun borrarTodo()

    @Transaction
    open suspend fun reemplazar(items: List<TarjetaEntity>) {
        borrarTodo()
        insertar(items)
    }
}

@Dao
abstract class TicketDao {
    @Query("SELECT * FROM tickets ORDER BY orden")
    abstract fun observar(): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<TicketEntity>)

    @Query("DELETE FROM tickets")
    abstract suspend fun borrarTodo()

    @Transaction
    open suspend fun reemplazar(items: List<TicketEntity>) {
        borrarTodo()
        insertar(items)
    }
}

@Dao
abstract class RenovacionDao {
    @Query("SELECT * FROM renovaciones ORDER BY id")
    abstract fun observar(): Flow<List<RenovacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<RenovacionEntity>)

    @Query("DELETE FROM renovaciones")
    abstract suspend fun borrarTodo()

    @Query("DELETE FROM renovaciones WHERE id = :id")
    abstract suspend fun borrar(id: Int)

    @Transaction
    open suspend fun reemplazar(items: List<RenovacionEntity>) {
        borrarTodo()
        insertar(items)
    }
}

@Dao
abstract class UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY nombre")
    abstract fun observar(): Flow<List<UsuarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(items: List<UsuarioEntity>)

    @Query("DELETE FROM usuarios")
    abstract suspend fun borrarTodo()

    @Transaction
    open suspend fun reemplazar(items: List<UsuarioEntity>) {
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
