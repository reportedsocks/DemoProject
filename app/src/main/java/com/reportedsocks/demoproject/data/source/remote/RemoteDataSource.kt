package com.reportedsocks.demoproject.data.source.remote

import com.reportedsocks.demoproject.data.DataSource
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents network and implements DataSource
 * @see DataSource
 */
@Singleton
class RemoteDataSource @Inject constructor(
    private val githubApi: GithubApi
) : DataSource {

    /**
     * By default will use IO dispatcher to perform operations,
     * other dispatcher can be specified in constructor
     */
    constructor(githubApi: GithubApi, dispatcher: CoroutineDispatcher) : this(githubApi) {
        this.dispatcher = dispatcher
    }

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO


    override suspend fun getUsers(id: Int): Result<List<User>> {
        return withContext(dispatcher) {
            try {
                val response = githubApi.getUsers(id)

                if (response.isSuccessful && response.body() != null) {
                    val result = Result.Success(response.body()!!)
                    result
                } else if (response.isSuccessful) {
                    val result = Result.Loading
                    result
                } else {
                    val result = Result.Error(Exception(response.message()), true)
                    result
                }
            } catch (e: Exception) {
                Result.Error(e, true)
            }
        }
    }

    override fun getUsersSync(id: Int): Result<List<User>> {
        return try {
            val response = githubApi.getUsersSync(id).execute()

            if (response.isSuccessful && response.body() != null) {
                val result = Result.Success(response.body()!!)
                result
            } else if (response.isSuccessful) {
                val result = Result.Loading
                result
            } else {
                val result = Result.Error(Exception(response.message()), true)
                result
            }
        } catch (e: Exception) {
            Result.Error(e, true)
        }

    }

    override fun getAllUsersSync(id: Int): Result<List<User>> {
        //not needed
        return Result.Error(java.lang.Exception("Not implemented"))
    }

    override fun getAllUsersWithIdSmallerSync(id: Int): Result<List<User>> {
        //not needed
        return Result.Error(java.lang.Exception("Not implemented"))
    }


    override suspend fun saveUser(user: User) {
        //not needed
    }

    override fun saveUserSync(user: User) {
        //not needed
    }

    override suspend fun getUser(id: Int): Result<User> {
        //not needed
        return Result.Error(java.lang.Exception("Not implemented"))
    }
}