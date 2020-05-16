package com.reportedsocks.demoproject.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val application: Application) {

    @Singleton
    @Provides
    fun getContext(): Context {
        return application.applicationContext
    }

}