package com.reportedsocks.demoproject.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.reportedsocks.demoproject.data.DataSource
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val usersDao: UsersDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSource {

    override fun observeUsers(): LiveData<Result<List<User>>> {
        return usersDao.observeUsers().map {
            Result.Success(it)
        }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return withContext(dispatcher) {
            try {
                Result.Success(usersDao.getUsers())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getUsersFromId(id: Int): Result<List<User>> {
        return getUsers()
    }

    override suspend fun refreshUsers() {
        // not needed
    }

    override suspend fun deleteUsers() {
        withContext(dispatcher) {
            usersDao.deleteUsers()
        }
    }

    override suspend fun saveUser(user: User) {
        withContext(dispatcher) {
            usersDao.insertUser(user)
        }
    }

}