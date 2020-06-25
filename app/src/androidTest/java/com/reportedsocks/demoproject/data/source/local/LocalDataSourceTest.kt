package com.reportedsocks.demoproject.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_USER
import com.reportedsocks.demoproject.ui.util.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {

    private lateinit var dataBase: AppDatabase
    private lateinit var localDataSource: LocalDataSource

    private val user = User(1, "login1", "avatar1", "html1", "repo1", ITEM_TYPE_USER, false)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        localDataSource = LocalDataSource(dataBase.usersDao(), Dispatchers.Main)
    }

    @After
    fun close() {
        dataBase.close()
    }

    @Test
    fun getUsers() = runBlocking {
        // When db contains more that 1 page
        fillDb(PAGE_SIZE + 2)
        // Then getUsers() return only one page
        val result = localDataSource.getUsers(INITIAL_KEY)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data.size, `is`(PAGE_SIZE))
    }

    @Test
    fun getUsersSync() = runBlocking {
        // When db contains more that 1 page
        fillDb(PAGE_SIZE + 2)
        // Then getUsers() return only one page
        val result = localDataSource.getUsersSync(INITIAL_KEY)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data.size, `is`(PAGE_SIZE))
    }

    @Test
    fun getUser() = runBlocking {
        // When db contains users
        fillDb(PAGE_SIZE)
        // Then it can be retrieved correctly
        val result = localDataSource.getUser(1)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data, `is`(user))
    }

    @Test
    fun getAllUsers() = runBlocking {
        // When db contains users
        fillDb(PAGE_SIZE + 5)
        // Then all users are returned
        val result = localDataSource.getAllUsers(INITIAL_KEY)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data.size, `is`(PAGE_SIZE + 5))
    }

    @Test
    fun getAllUsersSync() = runBlocking {
        // When db contains users
        fillDb(PAGE_SIZE + 5)
        // Then all users are returned
        val result = localDataSource.getAllUsersSync(INITIAL_KEY)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data.size, `is`(PAGE_SIZE + 5))
    }

    @Test
    fun getAllUsersWithIdSmallerSync() = runBlocking {
        // When db contains users
        fillDb(PAGE_SIZE)
        // Then
        val result = localDataSource.getAllUsersWithIdSmallerSync(10)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        for (dbUser in data) {
            assertThat(dbUser.id, `is`(lessThanOrEqualTo(10)))
        }
    }

    @Test
    fun saveUser() = runBlocking {
        // When user was saved
        localDataSource.saveUser(user)
        // Then it can be retrieved
        val result = localDataSource.getUser(user.id)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data, `is`(user))
    }

    @Test
    fun saveUserSync() = runBlocking {
        // When user was saved
        localDataSource.saveUserSync(user)
        // Then it can be retrieved
        val result = localDataSource.getUser(user.id)
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val data = (result as Result.Success).data
        assertThat(data, `is`(user))
    }

    private suspend fun fillDb(n: Int) {
        for (id in 1..n) {
            dataBase.usersDao().insertUser(User(id,
                "login$id",
                "avatar$id",
                "html$id",
                "repo$id",
                ITEM_TYPE_USER,
                false))
        }
    }
}