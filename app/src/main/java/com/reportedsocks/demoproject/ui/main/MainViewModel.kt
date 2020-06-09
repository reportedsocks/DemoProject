package com.reportedsocks.demoproject.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.DataRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _items: LiveData<List<User>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                dataRepository.refreshUsers()
                _dataLoading.value = false
            }
        }
        dataRepository.observeUsers().switchMap { checkResults(it) }
    }
    val items: LiveData<List<User>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    init {
        loadUsers(true)
    }

    private fun checkResults(usersResults: Result<List<User>>): LiveData<List<User>> {
        val result = MutableLiveData<List<User>>()
        if (usersResults is Result.Success) {
            result.value = usersResults.data
            Log.d("MyLogs", "results in vm: ${usersResults.data}")
            // TODO set error to false
        } else {
            result.value = emptyList()
            // TODO show error
            Log.d("MyLogs", "error in vm")
        }
        return result
    }

    fun loadUsers(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun refresh() {
        _forceUpdate.value = true
    }
}