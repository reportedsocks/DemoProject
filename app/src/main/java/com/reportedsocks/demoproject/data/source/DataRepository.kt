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

    suspend fun loadAndSaveUsers(id: Int): List<User> {
        updateUsersFromRemoteDataSource(id)
        return getUsersFromLocalDataSource(id)
    }

    private suspend fun updateUsersFromRemoteDataSource(id: Int) {
        _dataLoading.postValue(true)
        val result = remoteDataSource.getUsers(id)
        if (result is Result.Success) {
            Log.d("MyLogs", "DataRepository remote result: ${result.data}")

            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }

            for (user in result.data) {
                localDataSource.saveUser(user)
            }
        } else if (result is Result.Error) {
            Log.d("MyLogs", "DataRepository error loading remote data ${result.exception}")
            //TODO handle error

            // java.lang.Exception: rate limit exceeded
        }
    }

    private suspend fun getUsersFromLocalDataSource(id: Int): List<User> {
        val result = localDataSource.getUsers(id)

        return if (result is Result.Success) {
            Log.d("MyLogs", "DataRepository local result: ${result.data}")

            if (result.data.isNotEmpty() && result.data.last().id > lastLoadedItemId) {
                lastLoadedItemId = result.data.last().id
            }

            _dataLoading.postValue(false)
            val filtered = filterItems(result.data)
            _isEmpty.value = filtered.isEmpty()
            filtered
        } else {
            Log.d("MyLogs", "DataRepository error loading local data")
            _dataLoading.postValue(false)
            emptyList()
            //TODO handle error
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