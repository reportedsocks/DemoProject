package com.reportedsocks.demoproject.data.source.local

import androidx.room.*
import com.reportedsocks.demoproject.data.User

@Dao
interface UsersDao {

    @Query("SELECT * FROM users WHERE id > :id")
    suspend fun getUsers(id: Int): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User): Int

    @Query("DELETE FROM users")
    suspend fun deleteUsers()
}