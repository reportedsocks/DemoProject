package com.reportedsocks.demoproject.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reportedsocks.demoproject.data.DataSource
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val githubApi: GithubApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSource {

    private val observableUsers = MutableLiveData<Result<List<User>>>()

    override fun observeUsers(): LiveData<Result<List<User>>> {
        return observableUsers
    }

    override suspend fun getUsers(): Result<List<User>> {
        return withContext(dispatcher) {
            githubApi.getUsers()
        }
    }

    override suspend fun getUsersFromId(id: Int): Result<List<User>> {
        return withContext(dispatcher) {
            githubApi.getUsersFromId(id)
        }
    }

    override suspend fun refreshUsers() {
        observableUsers.value = getUsers()
    }

    override suspend fun deleteUsers() {
        // not needed
    }

    override suspend fun saveUser(user: User) {
        //not needed
    }

}