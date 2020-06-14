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
        Log.d("MyLogs", "UserBoundaryCallback.onZeroItemsLoaded() called")
        //scope.launch { dataRepository.refreshUsers(0) }

    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {

        viewModel.viewModelScope.launch {
            Log.d(
                "MyLogs",
                "UserBoundaryCallback.onItemAtEndLoaded() called, lastId: ${dataRepository.lastLoadedItemId}, dataloading: ${dataRepository.dataLoading.value}"
            )
            /*if(dataRepository.dataLoading.value != true){
                dataRepository.boundaryCallbackWasCalled = true
                val result = dataRepository.peekUsersIfAvailable(dataRepository.lastLoadedItemId)
                if (result.isNotEmpty()) {
                    viewModel.refresh()
                }
                // This code would make app to keep loading until it has enough items of certain type,
                // but github just doesn't provide enough of "organisation" type users so it will result
                // in extremely long search which will be a bad UX. For now user can update page manually
                // by swiping down for refresh, if he wants to find more items
                *//*else {
                    onItemAtEndLoaded(itemAtEnd)
                }*//*
            }*/
        }
    }
}