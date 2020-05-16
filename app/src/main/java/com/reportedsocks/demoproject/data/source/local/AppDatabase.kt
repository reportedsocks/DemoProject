package com.reportedsocks.demoproject.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reportedsocks.demoproject.data.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao
}