package com.reportedsocks.demoproject.data

interface DataSource {

    suspend fun getUsers(id: Int): Result<List<User>>

    fun getUsersSync(id: Int): Result<List<User>>

    fun getAllUsersSync(id: Int): Result<List<User>>

    fun getAllUsersWithIdSmaller(id: Int): Result<List<User>>

    suspend fun saveUser(user: User)

    fun saveUserSync(user: User)
}