package com.reportedsocks.demoproject.di.modules

import android.content.Context
import androidx.room.Room
import com.reportedsocks.demoproject.data.source.local.AppDatabase
import com.reportedsocks.demoproject.data.source.local.UsersDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context, AppDatabase::class.java, "demo_app_db"
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideUsersDao(appDatabase: AppDatabase): UsersDao {
        return appDatabase.usersDao()
    }
}