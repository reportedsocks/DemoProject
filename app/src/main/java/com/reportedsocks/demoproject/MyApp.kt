package com.reportedsocks.demoproject

import android.app.Application
import com.reportedsocks.demoproject.di.modules.ContextModule
import com.reportedsocks.demoproject.di.modules.DatabaseModule
import com.reportedsocks.demoproject.di.modules.RetrofitModule
import com.reportedsocks.demoproject.di.modules.ViewModelModule
import com.reportedsocks.demoproject.ui.details.UserDetailsFragment
import com.reportedsocks.demoproject.ui.main.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, DatabaseModule::class, ContextModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(fragment: MainFragment)
    fun inject(fragment: UserDetailsFragment)
}

class MyApp : Application() {
    var appComponent: ApplicationComponent =
        DaggerApplicationComponent.builder().contextModule(ContextModule(this)).build()
}