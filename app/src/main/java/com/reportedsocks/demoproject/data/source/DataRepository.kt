package com.reportedsocks.demoproject.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.local.LocalDataSource
import com.reportedsocks.demoproject.data.source.remote.RemoteDataSource
import com.reportedsocks.demoproject.ui.main.UsersFilterType
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_ORGANISATION
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_USER
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataRepository is responsible for querying local and remote data sources
 */
@Singleton
class DataRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource, private val localDataSource: LocalDataSource
) {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    // true if last loaded page is empty
    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    private var _loadingError = MutableLiveData<Result.Error?>()
    val loadingError: LiveData<Result.Error?> = _loadingError

    private var lastLoadedItemId = INITIAL_KEY

    var boundaryCallbackWasCalled: Boolean = false

    var currentFiltering = UsersFilterType.ALL

    /**
     * Get user by id from db
     * @param id Integer id of user
     */
    suspend fun getUser(id: Int): Result<User> {
        return localDataSource.getUser(id)
    }

    /**
     * Query page of users from remoteDataSource,
     * save it to db and load results from there
     */
    suspend fun loadAndSaveUsers(id: Int): List<User> {
        updateUsersFromRemoteDataSource(id)
        return getUsersFromLocalDataSource(id)
    }

    /**
     * Synchronously query page of users from remoteDataSource,
     * save it to db and load results from there
     */
    private fun loadAndSaveUsersSync(id: Int): List<User> {
        updateUsersFromRemoteDataSourceSync(id)
        return getUsersFromLocalDataSourceSync(id)
    }

    /**
     * Check if db contains more items after lastLoadedItem and return it
     */
    suspend fun peekUsersIfAvailable(): List<User> {
        val result = localDataSource.getUsers(lastLoadedItemId)
        return if (result is Result.Success) {
            filterItems(result.data)
        } else {
            emptyList()
        }
    }

    /**
     * Synchronously load all users with id smaller than given a next page after this id
     */
    fun loadInitialUsers(id: Int): List<User> {
        // update all preceding users from network
        var lastLoadedUser = updateUsersFromRemoteDataSourceSync(id)
        lastLoadedUser?.let {
            while (it < id) {
                lastLoadedUser = updateUsersFromRemoteDataSourceSync(id)
            }
        }
        // get all preceding users from db
        val previousItems = getAllUsersWithIdSmallerSync(id)
        // get next page from db
        val nextPage = loadAndSaveUsersSync(id)
        // return combined list
        val result = previousItems.toMutableList()
        result.addAll(nextPage)
        return result

    }

    /**
     * Load next page from network and save results
     */
    private suspend fun updateUsersFromRemoteDataSource(id: Int): Int? {
        _dataLoading.postValue(true)
        val result = remoteDataSource.getUsers(id)
        return when (result) {
            is Result.Success -> {
                _loadingError.postValue(null)
                // save loaded users in db
                for (user in result.data) {
                    localDataSource.saveUser(user)
                }
                result.data.last().id
            }
            is Result.Error -> {
                _loadingError.postValue(result)
                null
            }
            else -> null
        }
    }

    /**
     * Synchronously load next page from network and save results
     */
    private fun updateUsersFromRemoteDataSourceSync(id: Int): Int? {
        _dataLoading.postValue(true)
        val result = remoteDataSource.getUsersSync(id)
        return when (result) {
            is Result.Success -> {
                _loadingError.postValue(null)

                for (user in result.data) {
                    localDataSource.saveUserSync(user)
                }
                result.data.last().id
            }
            is Result.Error -> {
                _loadingError.postValue(result)
                null
            }
            else -> null
        }
    }

    /**
     * Load next page from db and filter it
     */
    private suspend fun getUsersFromLocalDataSource(id: Int): List<User> {
        val result = if (!boundaryCallbackWasCalled) {
            localDataSource.getUsers(id)
        } else {
            boundaryCallbackWasCalled = false
            localDataSource.getAllUsersSync(id)
        }

        return if (result is Result.Success) {
            // update lastLoadedItemId
            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }
            // filter results
            val filtered = filterItems(result.data)
            _loadingError.postValue(null)
            _dataLoading.postValue(false)
            _isEmpty.postValue(filtered.isEmpty())
            filtered
        } else {
            _loadingError.postValue(result as? Result.Error)
            _dataLoading.postValue(false)
            emptyList()
        }
    }

    /**
     * Synchronously load next page from db and filter it
     */
    private fun getUsersFromLocalDataSourceSync(id: Int): List<User> {
        val result = if (!boundaryCallbackWasCalled) {
            localDataSource.getUsersSync(id)
        } else {
            boundaryCallbackWasCalled = false
            localDataSource.getAllUsersSync(id)
        }

        return if (result is Result.Success) {
            // update lastLoadedItemId
            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }
            // filter results
            val filtered = filterItems(result.data)
            _loadingError.postValue(null)
            _dataLoading.postValue(false)
            _isEmpty.postValue(filtered.isEmpty())
            filtered
        } else {
            _loadingError.postValue(result as? Result.Error)
            _dataLoading.postValue(false)
            emptyList()
        }
    }

    /**
     * Synchronously load all users preceding to id from db
     */
    private fun getAllUsersWithIdSmallerSync(id: Int): List<User> {
        val result = localDataSource.getAllUsersWithIdSmallerSync(id)

        return if (result is Result.Success) {
            // update lastLoadedItemId
            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }
            // filter results
            val filtered = filterItems(result.data)
            _loadingError.postValue(null)
            _dataLoading.postValue(false)
            _isEmpty.postValue(filtered.isEmpty())
            filtered
        } else {
            _loadingError.postValue(result as? Result.Error)
            _dataLoading.postValue(false)
            emptyList()
        }
    }

    /**
     * Filter a list of users corresponding to current filtering parameter
     */
    private fun filterItems(users: List<User>): List<User> {
        val usersToShow = ArrayList<User>()
        for (user in users) {
            when (currentFiltering) {
                UsersFilterType.ALL -> usersToShow.add(user)
                UsersFilterType.USER -> if (user.type == ITEM_TYPE_USER) {
                    usersToShow.add(user)
                }
                UsersFilterType.ORGANISATION -> if (user.type == ITEM_TYPE_ORGANISATION) {
                    usersToShow.add(user)
                }
            }
        }
        return usersToShow
    }
}