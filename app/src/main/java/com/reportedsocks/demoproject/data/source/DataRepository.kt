package com.reportedsocks.demoproject.data.source

import androidx.lifecycle.LiveData
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.main.UsersFilterType

interface DataRepository {

    val dataLoading: LiveData<Boolean>

    /**
     * true if last loaded page is empty
     */
    val isEmpty: LiveData<Boolean>

    val loadingError: LiveData<Result.Error?>

    /**
     * If set to true, next call to getUsersFromLocalDataSourceSync will load all items from db
     */
    var boundaryCallbackWasCalled: Boolean

    var currentFiltering: UsersFilterType

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