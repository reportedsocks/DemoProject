package com.reportedsocks.demoproject

import com.reportedsocks.demoproject.data.source.DataRepository
import com.reportedsocks.demoproject.data.source.FakeDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FakeRepositoryModule {
    @Provides
    @Singleton
    fun provideDataRepository(
    ): DataRepository {
        return FakeDataRepository()
    }
}