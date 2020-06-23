package com.reportedsocks.demoproject.data.source

import androidx.paging.ItemKeyedDataSource
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * DataSource used in paging library, extends ItemKeyedDataSource
 */
class PagedDataSource(
    private val scope: CoroutineScope, private val dataRepository: DataRepository
) : ItemKeyedDataSource<Int, User>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>, callback: LoadInitialCallback<User>
    ) {
        // synchronously load users preceding to initialKey and a page afterwards
        // this is called when dataSource is restored after invalidation
        val result = dataRepository.loadInitialUsers(params.requestedInitialKey ?: INITIAL_KEY)
        callback.onResult(result)
    }

    override fun loadAfter(
        params: LoadParams<Int>, callback: LoadCallback<User>
    ) {
        // load a page after requested key (user.id)
        scope.launch {
            val result = dataRepository.loadAndSaveUsers(params.key)
            callback.onResult(result)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>, callback: LoadCallback<User>
    ) {
        // not needed
    }

    override fun getKey(item: User): Int {
        return item.id
    }

}