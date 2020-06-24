package com.reportedsocks.demoproject.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.DataRepository
import com.reportedsocks.demoproject.data.source.paging.PagedDataSource
import com.reportedsocks.demoproject.data.source.paging.UserBoundaryCallback
import com.reportedsocks.demoproject.ui.util.Event
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import com.reportedsocks.demoproject.ui.util.PAGE_SIZE
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    val loadingError: LiveData<Result.Error?> = dataRepository.loadingError

    val dataLoading: LiveData<Boolean> = dataRepository.dataLoading

    var pagedItems: LiveData<PagedList<User>>
        private set

    private var currentFiltering = UsersFilterType.ALL

    private val _openUserEvent = MutableLiveData<Event<Int>>()
    val openUserEvent: LiveData<Event<Int>> = _openUserEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noItemsFilteringLabel = MutableLiveData<Int>()
    val noItemsFilteringLabel: LiveData<Int> = _noItemsFilteringLabel

    private val _noItemsIconRes = MutableLiveData<Int>()
    val noItemsIconRes: LiveData<Int> = _noItemsIconRes

    init {
        val config =
            PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()
        // initialize PagedList/ DataSource pair
        pagedItems = initializePagedListBuilder(config).setBoundaryCallback(UserBoundaryCallback(
            dataRepository,
            this)).setInitialLoadKey(INITIAL_KEY).build()
        setFiltering(currentFiltering)
    }

    val empty: LiveData<Boolean> = dataRepository.isEmpty.map { lastPageEmpty ->
        lastPageEmpty && pagedItems.value?.isEmpty() == true
    }

    /**
     * Apply filtering to items list
     */
    fun setFiltering(filterType: UsersFilterType) {
        currentFiltering = filterType
        // update filtering in dataRepository
        dataRepository.currentFiltering = filterType
        when (filterType) {
            UsersFilterType.ALL -> {
                setFilter(R.string.label_all,
                    R.string.no_items_all,
                    R.drawable.ic_baseline_not_interested_24)
            }
            UsersFilterType.USER -> {
                setFilter(R.string.label_users,
                    R.string.no_items_users,
                    R.drawable.ic_baseline_person_outline_24)
            }
            UsersFilterType.ORGANISATION -> {
                setFilter(R.string.label_organisations,
                    R.string.no_items_organisations,
                    R.drawable.ic_baseline_people_outline_24)
            }
        }
        // trigger reload of list
        pagedItems.value?.dataSource?.invalidate()
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noItemsLabelString: Int,
        @DrawableRes noItemsIconDrawable: Int
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noItemsFilteringLabel.value = noItemsLabelString
        _noItemsIconRes.value = noItemsIconDrawable
    }

    /**
     * Display error corresponding to Result.Error params
     */
    fun showError(error: Result.Error?) {
        error?.let {
            if (error.isNetworkException) {
                showSnackbarMessage(R.string.error_updating_items)
            } else {
                showSnackbarMessage(R.string.error_loading_items)
            }
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun initializePagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Int, User> {
        // factory for my PagedDataSource
        val dataSourceFactory = object : DataSource.Factory<Int, User>() {
            override fun create(): PagedDataSource {
                return PagedDataSource(
                    viewModelScope,
                    dataRepository)
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }

    /**
     * Trigger list reload
     */
    fun refresh() {
        pagedItems.value?.dataSource?.invalidate()
    }

    /**
     * Trigger navigation to UserDetailsFragment
     */
    fun openUser(id: Int) {
        _openUserEvent.value = Event(id)
    }
}