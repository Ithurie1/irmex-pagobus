package com.irmex.admin.di

import android.content.Context
import androidx.room.Room
import com.irmex.admin.data.local.IrmexDatabase
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
        Room.databaseBuilder(context, IrmexDatabase::class.java, "irmex_admin.db")
            .fallbackToDestructiveMigration() // es solo caché: se vuelve a descargar
            .build()

    @Provides fun provideTarjetaDao(db: IrmexDatabase) = db.tarjetaDao()
    @Provides fun provideTicketDao(db: IrmexDatabase) = db.ticketDao()
    @Provides fun provideRenovacionDao(db: IrmexDatabase) = db.renovacionDao()
    @Provides fun provideUsuarioDao(db: IrmexDatabase) = db.usuarioDao()
    @Provides fun providePerfilDao(db: IrmexDatabase) = db.perfilDao()
    @Provides fun provideResumenDao(db: IrmexDatabase) = db.resumenDao()
    @Provides fun provideAccesoDao(db: IrmexDatabase) = db.accesoDao()
}
