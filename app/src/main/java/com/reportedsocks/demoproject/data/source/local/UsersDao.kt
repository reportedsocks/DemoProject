package com.reportedsocks.demoproject.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reportedsocks.demoproject.data.User

@Dao
interface UsersDao {
    @Query("SELECT * FROM users WHERE id > :id LIMIT :pageSize")
    suspend fun getUsers(id: Int, pageSize: Int): List<User>

    @Query("SELECT * FROM users WHERE id > :id LIMIT :pageSize")
    fun getUsersSync(id: Int, pageSize: Int): List<User>

    @Query("SELECT * FROM users WHERE id == :id LIMIT 1")
    suspend fun getUser(id: Int): User

    @Query("SELECT * FROM users WHERE id > :id")
    suspend fun getAllUsers(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id > :id")
    fun getAllUsersSync(id: Int): List<User>

    @Query("SELECT * FROM users WHERE id <= :id")
    fun getAllUsersWithIdSmallerSync(id: Int): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserSync(user: User)

}