package com.reportedsocks.demoproject.data.source

import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User

interface DataRepository {
    /**
     * Get user by id from db
     * @param id Integer id of user
     */
    suspend fun getUser(id: Int): Result<User>

    /**
     * Query page of users from remoteDataSource,
     * save it to db and load results from there
     */
    suspend fun loadAndSaveUsers(id: Int): List<User>

    /**
     * Check if db contains more items after lastLoadedItem and return it
     */
    suspend fun peekUsersIfAvailable(): List<User>

    /**
     * Synchronously load all users with id smaller than given a next page after this id
     */
    fun loadInitialUsers(id: Int): List<User>
}