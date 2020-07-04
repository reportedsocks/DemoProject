package com.reportedsocks.demoproject

import android.app.Application
import com.reportedsocks.demoproject.di.modules.*
import com.reportedsocks.demoproject.ui.details.UserDetailsFragment
import com.reportedsocks.demoproject.ui.main.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, DatabaseModule::class, ContextModule::class, ViewModelModule::class, RepositoryModule::class])
interface ApplicationComponent {
    fun inject(fragment: MainFragment)
    fun inject(fragment: UserDetailsFragment)
}

class MyDefaultApp : Application(), MyApp {
    override var appComponent: ApplicationComponent =
        DaggerApplicationComponent.builder().contextModule(ContextModule(this)).build()
}

interface MyApp {
    var appComponent: ApplicationComponent
}

// feature1
// feature2