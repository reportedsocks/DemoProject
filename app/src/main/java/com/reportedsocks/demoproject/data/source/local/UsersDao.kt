package com.reportedsocks.demoproject.data.source.local

import androidx.room.*
import com.reportedsocks.demoproject.data.User

@Dao
interface UsersDao {
    @Query("SELECT * FROM users WHERE id > :id LIMIT 30")
    suspend fun getUsers(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id > :id LIMIT 30")
    fun getUsersSync(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id > :id")
    suspend fun getAllUsers(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id > :id")
    fun getAllUsersSync(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id <= :id")
    suspend fun getAllUsersWithIdSmaller(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id <= :id")
    fun getAllUsersWithIdSmallerSync(id: Int): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserSync(user: User)

    @Update
    suspend fun updateUser(user: User): Int

    @Query("DELETE FROM users")
    suspend fun deleteUsers()
}