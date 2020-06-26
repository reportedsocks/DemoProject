package com.reportedsocks.demoproject.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.main.UsersFilterType
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_ORGANISATION
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_USER

class FakeDataRepository : DataRepository {

    private val _dataLoading = MutableLiveData<Boolean>()
    override val dataLoading: LiveData<Boolean> = _dataLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    override val isEmpty: LiveData<Boolean> = _isEmpty

    private var _loadingError = MutableLiveData<Result.Error?>()
    override val loadingError: LiveData<Result.Error?> = _loadingError

    override var boundaryCallbackWasCalled: Boolean = false

    override var currentFiltering = UsersFilterType.ALL

    private val user1 =
        User(1, "login1", "avatar1", "html1", "repo1", ITEM_TYPE_USER, false)
    private val user2 =
        User(2, "login2", "avatar2", "html2", "repo2", ITEM_TYPE_ORGANISATION, false)
    private val user3 =
        User(3, "login3", "avatar3", "html3", "repo3", ITEM_TYPE_ORGANISATION, false)
    private val user4 =
        User(4, "login4", "avatar4", "html4", "repo4", ITEM_TYPE_USER, false)
    private val initialList = listOf(user1, user2)
    private val nextPage = listOf(user3, user4)
    private var wasCalledBefore = false

    override suspend fun getUser(id: Int): Result<User> {
        return Result.Success(user1)
    }

    override suspend fun loadAndSaveUsers(id: Int): List<User> {
        val result: List<User>
        if (!wasCalledBefore) {
            _dataLoading.postValue(true)
            result = filterItems(nextPage)
            _isEmpty.postValue(result.isEmpty())
            wasCalledBefore = true
            _dataLoading.postValue(false)
        } else {
            _dataLoading.postValue(true)
            result = emptyList()
            _isEmpty.postValue(result.isEmpty())
            _dataLoading.postValue(false)
        }
        return result
    }

    override suspend fun peekUsersIfAvailable(): List<User> {
        return emptyList()
    }

    override fun loadInitialUsers(id: Int): List<User> {
        _dataLoading.postValue(true)
        val result = filterItems(initialList)
        _isEmpty.postValue(result.isEmpty())
        _dataLoading.postValue(false)
        return result
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