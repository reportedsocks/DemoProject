package com.reportedsocks.demoproject.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.DataRepository
import com.reportedsocks.demoproject.util.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserDetailsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
    private var _userItem = MutableLiveData<User>()
    val userItem: LiveData<User> = _userItem

    private var _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var _browserRedirectEvent = MutableLiveData<Event<String>>()
    val browserRedirectEvent: LiveData<Event<String>> = _browserRedirectEvent

    /**
     * Load User from db with given id, then update LiveData
     */
    fun start(id: Int) {
        viewModelScope.launch {
            val result = dataRepository.getUser(id)
            if (result is Result.Success) {
                _userItem.value = result.data
            } else {
                _snackbarText.value = Event(R.string.error_loading_user)
            }
        }
    }

    /**
     * Trigger event to open a url in browser
     */
    fun openBrowser() {
        userItem.value?.let { user ->
            _browserRedirectEvent.value = Event(user.htmlUrl)
        }
    }
}