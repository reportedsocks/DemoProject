package com.reportedsocks.demoproject

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.reportedsocks.demoproject.di.modules.ContextModule
import com.reportedsocks.demoproject.di.modules.DatabaseModule
import com.reportedsocks.demoproject.di.modules.RetrofitModule
import com.reportedsocks.demoproject.di.modules.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RetrofitModule::class, DatabaseModule::class, ContextModule::class, ViewModelModule::class, FakeRepositoryModule::class]
)
interface TestApplicationComponent : ApplicationComponent

class MyAppTest : Application(), MyApp {
    override var appComponent: ApplicationComponent =
        DaggerTestApplicationComponent.builder().contextModule(ContextModule(this)).build()
}

class MyTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MyAppTest::class.java.name, context)
    }
}