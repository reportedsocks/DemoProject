package com.reportedsocks.demoproject.data.source

import androidx.lifecycle.LiveData
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.local.LocalDataSource
import com.reportedsocks.demoproject.data.source.remote.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getUsers(forceUpdate: Boolean): Result<List<User>> {
        if (forceUpdate) {
            try {
                updateUsersFromRemoteDataSource()
            } catch (e: Exception) {
                return Result.Error(e)
            }
        }
        return localDataSource.getUsers()
    }

    fun observeUsers(): LiveData<Result<List<User>>> {
        return remoteDataSource.observeUsers()
    }

    suspend fun refreshUsers() {
        updateUsersFromRemoteDataSource()
    }

    private suspend fun updateUsersFromRemoteDataSource() {
        val remoteUsers = remoteDataSource.getUsers()
        if (remoteUsers is Result.Success) {
            localDataSource.deleteUsers()
            remoteUsers.data.forEach { user ->
                localDataSource.saveUser(user)
            }
        } else if (remoteUsers is Result.Error) {
            throw remoteUsers.exception
        }
    }
}