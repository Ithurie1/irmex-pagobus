package com.irmex.estudiante.di

import com.irmex.estudiante.data.repository.ArchivoRepositoryImpl
import com.irmex.estudiante.data.repository.AuthRepositoryImpl
import com.irmex.estudiante.data.repository.MovimientoRepositoryImpl
import com.irmex.estudiante.data.repository.PerfilRepositoryImpl
import com.irmex.estudiante.data.repository.RenovacionRepositoryImpl
import com.irmex.estudiante.data.repository.ReporteRepositoryImpl
import com.irmex.estudiante.data.repository.TarjetaRepositoryImpl
import com.irmex.estudiante.domain.repository.ArchivoRepository
import com.irmex.estudiante.domain.repository.AuthRepository
import com.irmex.estudiante.domain.repository.MovimientoRepository
import com.irmex.estudiante.domain.repository.PerfilRepository
import com.irmex.estudiante.domain.repository.RenovacionRepository
import com.irmex.estudiante.domain.repository.ReporteRepository
import com.irmex.estudiante.domain.repository.TarjetaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun auth(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun tarjeta(impl: TarjetaRepositoryImpl): TarjetaRepository
    @Binds abstract fun movimientos(impl: MovimientoRepositoryImpl): MovimientoRepository
    @Binds abstract fun reportes(impl: ReporteRepositoryImpl): ReporteRepository
    @Binds abstract fun renovacion(impl: RenovacionRepositoryImpl): RenovacionRepository
    @Binds abstract fun archivos(impl: ArchivoRepositoryImpl): ArchivoRepository
    @Binds abstract fun perfil(impl: PerfilRepositoryImpl): PerfilRepository
}
