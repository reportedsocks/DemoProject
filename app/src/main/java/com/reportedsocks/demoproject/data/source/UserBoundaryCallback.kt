package com.reportedsocks.demoproject.data.source

import android.util.Log
import androidx.paging.PagedList
import com.reportedsocks.demoproject.data.User
import kotlinx.coroutines.CoroutineScope

class UserBoundaryCallback(
    private val dataRepository: DataRepository,
    private val scope: CoroutineScope
) : PagedList.BoundaryCallback<User>() {
    override fun onZeroItemsLoaded() {
        Log.d("MyLogs", "UserBoundaryCallback.onZeroItemsLoaded() called")
        //scope.launch { dataRepository.refreshUsers(0) }

    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {
        Log.d("MyLogs", "UserBoundaryCallback.onItemAtEndLoaded() called")
        //TODO try to load more items after it was called
        //scope.launch { dataRepository.refreshUsers(itemAtEnd.id) }
    }
}