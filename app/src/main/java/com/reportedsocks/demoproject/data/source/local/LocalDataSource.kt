package com.reportedsocks.demoproject.data.source.local

import android.util.Log
import com.reportedsocks.demoproject.data.DataSource
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val usersDao: UsersDao
) : DataSource {

    constructor(usersDao: UsersDao, dispatcher: CoroutineDispatcher) : this(usersDao) {
        this.dispatcher = dispatcher
    }

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    /*override fun observeUsers(): LiveData<Result<List<User>>> {
        return usersDao.observeUsers().map {
            Result.Success(it)
        }
    }*/

    override suspend fun getUsers(id: Int): Result<List<User>> {
        Log.d("MyLogs", "Trying load users from localDataSource")
        return withContext(dispatcher) {
            try {
                Result.Success(usersDao.getUsers(id))
            } catch (e: Exception) {
                Result.Error(e, false)
            }
        }
    }

    /*suspend fun getAllUsers(): androidx.paging.DataSource.Factory<Int, User>{
        return usersDao.getAllUsers()
    }*/

    /*override suspend fun getUsersFromId(id: Int): Result<List<User>> {
        return getUsers()
    }*/

    /*override suspend fun refreshUsers() {
        // not needed
    }*/

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