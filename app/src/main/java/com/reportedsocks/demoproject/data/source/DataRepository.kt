package com.reportedsocks.demoproject.data.source

import android.util.Log
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

class DataRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    var currentFiltering = UsersFilterType.ALL

    var lastLoadedItemId = INITIAL_KEY

    private var _loadingError = MutableLiveData<Result.Error?>()
    val loadingError: LiveData<Result.Error?> = _loadingError

    var boundaryCallbackWasCalled: Boolean = false


    suspend fun loadAndSaveUsers(id: Int): List<User> {
        updateUsersFromRemoteDataSource(id)
        return getUsersFromLocalDataSource(id)
    }

    private fun loadAndSaveUsersSync(id: Int): List<User> {
        updateUsersFromRemoteDataSourceSync(id)
        return getUsersFromLocalDataSourceSync(id)
    }

    suspend fun peekUsersIfAvailable(id: Int): List<User> {
        val result = localDataSource.getUsers(id)
        return if (result is Result.Success) {
            filterItems(result.data)
        } else {
            emptyList()
        }
    }

    fun loadInitialUsers(id: Int): List<User> {
        // load all users with id < id
        // if no users? - > empty list?
        // how do i update them? make n network calls? - letssss go

        var lastLoadedUser = updateUsersFromRemoteDataSourceSync(id)
        lastLoadedUser?.let {
            while (it < id) {
                lastLoadedUser = updateUsersFromRemoteDataSourceSync(id)
            }
        }
        // okay i need to add next page here
        val previousItems = getAllUsersWithIdSmallerSync(id)
        val nextPage = loadAndSaveUsersSync(id)
        val result = previousItems.toMutableList()
        result.addAll(nextPage)
        Log.d("MyLogs", "initial load: $result")
        return result

    }

    private suspend fun updateUsersFromRemoteDataSource(id: Int): Int? {
        _dataLoading.postValue(true)
        val result = remoteDataSource.getUsers(id)
        return when (result) {
            is Result.Success -> {
                Log.d("MyLogs", "DataRepository remote result: ${result.data}")
                _loadingError.postValue(null)
                if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                    lastLoadedItemId = result.data.last().id
                }
                for (user in result.data) {
                    localDataSource.saveUser(user)
                }
                result.data.last().id
            }
            is Result.Error -> {
                _loadingError.postValue(result)
                Log.d("MyLogs", "DataRepository error loading remote data ${result.exception}")
                null
            }
            else -> null
        }
    }

    private fun updateUsersFromRemoteDataSourceSync(id: Int): Int? {
        _dataLoading.postValue(true)
        val result = remoteDataSource.getUsersSync(id)
        return when (result) {
            is Result.Success -> {
                Log.d("MyLogs", "DataRepository remote result: ${result.data}")
                _loadingError.postValue(null)
                if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                    lastLoadedItemId = result.data.last().id
                }
                for (user in result.data) {
                    localDataSource.saveUserSync(user)
                }
                result.data.last().id
            }
            is Result.Error -> {
                _loadingError.postValue(result)
                Log.d("MyLogs", "DataRepository error loading remote data ${result.exception}")
                null
            }
            else -> null
        }
    }

    private suspend fun getUsersFromLocalDataSource(id: Int): List<User> {
        val result = if (!boundaryCallbackWasCalled) {
            localDataSource.getUsers(id)
        } else {
            boundaryCallbackWasCalled = false
            localDataSource.getAllUsersSync(id)
        }

        return if (result is Result.Success) {


            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }

            val filtered = filterItems(result.data)
            Log.d("MyLogs", "DataRepository local result: $filtered")
            _loadingError.postValue(null)
            _dataLoading.postValue(false)
            _isEmpty.postValue(filtered.isEmpty())
            filtered
        } else {
            Log.d("MyLogs", "DataRepository error loading local data")
            _loadingError.postValue(result as? Result.Error)
            _dataLoading.postValue(false)
            emptyList()
        }
    }

    private fun getUsersFromLocalDataSourceSync(id: Int): List<User> {
        val result = if (!boundaryCallbackWasCalled) {
            localDataSource.getUsersSync(id)
        } else {
            boundaryCallbackWasCalled = false
            localDataSource.getAllUsersSync(id)
        }

        return if (result is Result.Success) {


            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }

            val filtered = filterItems(result.data)
            Log.d("MyLogs", "DataRepository local result: $filtered")
            _loadingError.postValue(null)
            _dataLoading.postValue(false)
            _isEmpty.postValue(filtered.isEmpty())
            filtered
        } else {
            Log.d("MyLogs", "DataRepository error loading local data")
            _loadingError.postValue(result as? Result.Error)
            _dataLoading.postValue(false)
            emptyList()
        }
    }

    private fun getAllUsersWithIdSmallerSync(id: Int): List<User> {
        val result = localDataSource.getAllUsersWithIdSmaller(id)

        return if (result is Result.Success) {

            Log.d("MyLogs", "DataRepository local result: ${result.data}")

            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }

            val filtered = filterItems(result.data)
            _loadingError.postValue(null)
            _dataLoading.postValue(false)
            _isEmpty.postValue(filtered.isEmpty())
            filtered
        } else {
            Log.d("MyLogs", "DataRepository error loading local data")
            _loadingError.postValue(result as? Result.Error)
            _dataLoading.postValue(false)
            emptyList()
        }
    }

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