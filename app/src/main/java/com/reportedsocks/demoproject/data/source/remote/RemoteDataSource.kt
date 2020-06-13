package com.reportedsocks.demoproject.data.source.remote

import android.util.Log
import com.reportedsocks.demoproject.data.DataSource
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val githubApi: GithubApi
) : DataSource {

    constructor(githubApi: GithubApi, dispatcher: CoroutineDispatcher) : this(githubApi) {
        this.dispatcher = dispatcher
    }

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    /*private val observableUsers = MutableLiveData<Result<List<User>>>()

    override fun observeUsers(): LiveData<Result<List<User>>> {
        return observableUsers
    }*/

    override suspend fun getUsers(id: Int): Result<List<User>> {
        Log.d("MyLogs", "Trying load users from remoteDataSource")
        return withContext(dispatcher) {
            try {
                val response = githubApi.getUsers(id)
                if (response.isSuccessful && response.body() != null) {
                    val result = Result.Success(response.body()!!)
                    //observableUsers.postValue(result)
                    result
                } else if (response.isSuccessful) {
                    val result = Result.Loading
                    //observableUsers.postValue(result)
                    result
                } else {
                    val result = Result.Error(
                        Exception(
                            response.message()
                        ),
                        true
                    )
                    //observableUsers.postValue(result)
                    result
                }
            } catch (e: Exception) {
                Result.Error(e, true)
            }
        }
    }

    /*override suspend fun getUsersFromId(id: Int): Result<List<User>> {
        return withContext(dispatcher) {
            githubApi.getUsersFromId(id)
        }
    }*/

    /*override suspend fun refreshUsers() { // TODO remove this
        observableUsers.value = getUsers()
    }*/

    override suspend fun deleteUsers() {
        // not needed
    }

    override suspend fun saveUser(user: User) {
        //not needed
    }


}