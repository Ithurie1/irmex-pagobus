package com.irmex.admin.di

import com.irmex.admin.data.repository.AccesoRepositoryImpl
import com.irmex.admin.data.repository.AuthRepositoryImpl
import com.irmex.admin.data.repository.DocumentoRepositoryImpl
import com.irmex.admin.data.repository.PerfilRepositoryImpl
import com.irmex.admin.data.repository.RenovacionRepositoryImpl
import com.irmex.admin.data.repository.ResumenRepositoryImpl
import com.irmex.admin.data.repository.TarjetaRepositoryImpl
import com.irmex.admin.data.repository.TicketRepositoryImpl
import com.irmex.admin.data.repository.UsuarioRepositoryImpl
import com.irmex.admin.domain.repository.AccesoRepository
import com.irmex.admin.domain.repository.AuthRepository
import com.irmex.admin.domain.repository.DocumentoRepository
import com.irmex.admin.domain.repository.PerfilRepository
import com.irmex.admin.domain.repository.RenovacionRepository
import com.irmex.admin.domain.repository.ResumenRepository
import com.irmex.admin.domain.repository.TarjetaRepository
import com.irmex.admin.domain.repository.TicketRepository
import com.irmex.admin.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun auth(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun tarjetas(impl: TarjetaRepositoryImpl): TarjetaRepository
    @Binds abstract fun tickets(impl: TicketRepositoryImpl): TicketRepository
    @Binds abstract fun renovaciones(impl: RenovacionRepositoryImpl): RenovacionRepository
    @Binds abstract fun usuarios(impl: UsuarioRepositoryImpl): UsuarioRepository
    @Binds abstract fun documentos(impl: DocumentoRepositoryImpl): DocumentoRepository
    @Binds abstract fun perfil(impl: PerfilRepositoryImpl): PerfilRepository
    @Binds abstract fun accesos(impl: AccesoRepositoryImpl): AccesoRepository
    @Binds abstract fun resumen(impl: ResumenRepositoryImpl): ResumenRepository
}
