package com.reportedsocks.demoproject.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.local.LocalDataSource
import com.reportedsocks.demoproject.data.source.remote.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    constructor(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        dispatcher: CoroutineDispatcher
    ) : this(remoteDataSource, localDataSource) {
        this.dispatcher = dispatcher
    }

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val observableUsers = MutableLiveData<Result<List<User>>>()

    //TODO refactor to UpdateUsersFromLocalDataSource()
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
        return observableUsers
    }

    suspend fun refreshUsers() {
        updateUsersFromRemoteDataSource()
    }

    private suspend fun updateUsersFromRemoteDataSource() {
        val remoteUsers = remoteDataSource.getUsers()
        Log.d("MyLogs", "response in dataRepository from remote: $remoteUsers")
        if (remoteUsers is Result.Success) {
            observableUsers.postValue(remoteUsers)
            localDataSource.deleteUsers()
            remoteUsers.data.forEach { user ->
                localDataSource.saveUser(user)
            }
        } else if (remoteUsers is Result.Error) {
            Log.d("MyLogs", "error in dataRepository when loading from remote, switching to local")
            observableUsers.postValue(getUsers(false))
        }
    }


}