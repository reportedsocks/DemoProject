package com.reportedsocks.demoproject.data.source

import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.main.MainViewModel
import kotlinx.coroutines.launch

/**
 * Boundary callback to be called when 0 items have been loaded
 * In my case it is called when a page doesn't have organisation and user filters list for organisations
 * To keep adequate UX it will load all items from db and see if there are any more organisations
 * to display that were previously loaded and show them
 * Technically it could search for more organisations but there are only 0-1 of such per page,
 * so it would take to much time and resource also there is a limit for API calls from github
 */
class UserBoundaryCallback(
    private val dataRepository: DataRepository, private val viewModel: MainViewModel
) : PagedList.BoundaryCallback<User>() {

    override fun onZeroItemsLoaded() {
        // not needed unless I need to add some type of error
    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {
        viewModel.viewModelScope.launch {
            if (dataRepository.dataLoading.value != true) {
                // check if there are any more users in db,
                // it will prevent the following load from getting fired more than once
                val result = dataRepository.peekUsersIfAvailable()
                if (result.isNotEmpty()) {
                    // trigger next call to repository to load users (remove one page limit)
                    dataRepository.boundaryCallbackWasCalled = true
                    viewModel.refresh()
                }
            }
        }
    }
}