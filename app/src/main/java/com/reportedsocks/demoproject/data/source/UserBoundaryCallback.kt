package com.reportedsocks.demoproject.data.source

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.main.MainViewModel
import kotlinx.coroutines.launch

class UserBoundaryCallback(
    private val dataRepository: DataRepository,
    private val viewModel: MainViewModel
) : PagedList.BoundaryCallback<User>() {

    override fun onZeroItemsLoaded() {
        // not needed unless I need to add some type of error
    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {

        viewModel.viewModelScope.launch {
            Log.d(
                "MyLogs",
                "UserBoundaryCallback.onItemAtEndLoaded() called, lastId: ${dataRepository.lastLoadedItemId}, dataloading: ${dataRepository.dataLoading.value}"
            )
            if (dataRepository.dataLoading.value != true) {
                dataRepository.boundaryCallbackWasCalled = true
                val result = dataRepository.peekUsersIfAvailable(dataRepository.lastLoadedItemId)
                if (result.isNotEmpty()) {
                    viewModel.refresh()
                }
            }
        }
    }
}