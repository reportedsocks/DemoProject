package com.reportedsocks.demoproject.di.modules

import com.reportedsocks.demoproject.data.source.DataRepository
import com.reportedsocks.demoproject.data.source.DefaultDataRepository
import com.reportedsocks.demoproject.data.source.local.LocalDataSource
import com.reportedsocks.demoproject.data.source.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideDataRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): DataRepository {
        return DefaultDataRepository(remoteDataSource, localDataSource)
    }
}