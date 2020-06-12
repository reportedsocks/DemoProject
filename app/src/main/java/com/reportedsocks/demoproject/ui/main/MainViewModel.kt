package com.reportedsocks.demoproject.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.DataRepository
import com.reportedsocks.demoproject.data.source.PagedDataSource
import com.reportedsocks.demoproject.data.source.UserBoundaryCallback
import com.reportedsocks.demoproject.ui.util.Event
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {


    /*private val _items: LiveData<List<User>> = _forceUpdate.switchMap { forceUpdate ->
        Log.d("MyLogs", "vm updating values, force: $forceUpdate")
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                //dataRepository.refreshUsers()
                _dataLoading.value = false
            }
        }
        dataRepository.observeUsers().switchMap { filterResults(it) }
    }*/
    //val items: LiveData<List<User>> = _items

    var pagedItems: LiveData<PagedList<User>>
        private set

    private var currentFiltering = UsersFilterType.ALL

    //private val _dataLoading = dataRepository.dataLoading
    val dataLoading: LiveData<Boolean> = dataRepository.dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noItemsFilteringLabel = MutableLiveData<Int>()
    val noItemsFilteringLabel: LiveData<Int> = _noItemsFilteringLabel

    private val _noItemsIconRes = MutableLiveData<Int>()
    val noItemsIconRes: LiveData<Int> = _noItemsIconRes

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(true)
            .build()

        pagedItems = initializePagedListBuilder(config)
            .setBoundaryCallback(UserBoundaryCallback(dataRepository, viewModelScope))
            .setInitialLoadKey(0)
            .build()
        setFiltering(currentFiltering)
    }

    val empty: LiveData<Boolean> = dataRepository.isEmpty.map { lastPageEmpty ->
        lastPageEmpty && pagedItems.value?.isEmpty() == true
    }

    fun setFiltering(filterType: UsersFilterType) {
        currentFiltering = filterType
        dataRepository.currentFiltering = filterType
        when (filterType) {
            UsersFilterType.ALL -> {
                setFilter(
                    R.string.label_all,
                    R.string.no_items_all,
                    R.drawable.ic_baseline_not_interested_24
                )
            }
            UsersFilterType.USER -> {
                setFilter(
                    R.string.label_users,
                    R.string.no_items_users,
                    R.drawable.ic_baseline_person_outline_24
                )
            }
            UsersFilterType.ORGANISATION -> {
                setFilter(
                    R.string.label_organisations,
                    R.string.no_items_organisations,
                    R.drawable.ic_baseline_people_outline_24
                )
            }
        }
        pagedItems.value?.dataSource?.invalidate()
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int, @StringRes noItemsLabelString: Int,
        @DrawableRes noItemsIconDrawable: Int
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noItemsFilteringLabel.value = noItemsLabelString
        _noItemsIconRes.value = noItemsIconDrawable
    }

    /*private fun filterResults(usersResults: Result<List<User>>): LiveData<List<User>> {
        val result = MutableLiveData<List<User>>()
        if (usersResults is Result.Success) {
            viewModelScope.launch {
                result.value = filterItems(usersResults.data, currentFiltering)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.error_loading_items)
        }
        return result
    }

    private fun filterItems(users: List<User>, filterType: UsersFilterType): List<User> {
        val usersToShow = ArrayList<User>()
        for (user in users) {
            when (filterType) {
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
    }*/

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun initializePagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Int, User> {
        val dataSourceFactory = object : DataSource.Factory<Int, User>() {
            override fun create(): PagedDataSource {
                return PagedDataSource(
                    viewModelScope,
                    dataRepository
                )
            }

        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }

    fun refresh() {
        pagedItems.value?.dataSource?.invalidate()
    }
}