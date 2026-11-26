package com.irmex.estudiante.di

import android.content.Context
import androidx.room.Room
import com.irmex.estudiante.data.local.IrmexDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IrmexDatabase =
        Room.databaseBuilder(context, IrmexDatabase::class.java, "irmex_estudiante.db")
            .fallbackToDestructiveMigration() // es solo caché: se vuelve a descargar
            .build()

    @Provides fun provideTarjetaDao(db: IrmexDatabase) = db.tarjetaDao()
    @Provides fun provideMovimientoDao(db: IrmexDatabase) = db.movimientoDao()
    @Provides fun provideReporteDao(db: IrmexDatabase) = db.reporteDao()
    @Provides fun providePerfilDao(db: IrmexDatabase) = db.perfilDao()
}
