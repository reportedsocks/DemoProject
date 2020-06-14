package com.reportedsocks.demoproject.data.source

import android.util.Log
import androidx.paging.ItemKeyedDataSource
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PagedDataSource(
    private val scope: CoroutineScope,
    private val dataRepository: DataRepository
) :
    ItemKeyedDataSource<Int, User>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<User>
    ) {
        Log.d(
            "MyLogs",
            "PagedRemoteDataSource.loadInitial() with key ${params.requestedInitialKey}"
        )

        val result = dataRepository.loadInitialUsers(params.requestedInitialKey ?: INITIAL_KEY)
        callback.onResult(result)

    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<User>
    ) {
        Log.d("MyLogs", "PagedRemoteDataSource.loadAfter() with key ${params.key}")
        scope.launch {
            val result = dataRepository.loadAndSaveUsers(params.key)
            callback.onResult(result)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<User>
    ) {
        // not needed
    }

    override fun getKey(item: User): Int {
        return item.id
    }

}