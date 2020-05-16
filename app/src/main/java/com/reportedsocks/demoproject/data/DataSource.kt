package com.reportedsocks.demoproject.data

import androidx.lifecycle.LiveData

interface DataSource {
    fun observeUsers(): LiveData<Result<List<User>>>
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUsersFromId(id: Int): Result<List<User>>
    suspend fun refreshUsers()
    suspend fun deleteUsers()
    suspend fun saveUser(user: User)
}