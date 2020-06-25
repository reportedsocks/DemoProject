package com.reportedsocks.demoproject.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_USER
import com.reportedsocks.demoproject.ui.util.PAGE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class UsersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataBase: AppDatabase

    private val user = User(1, "login1", "avatar1", "html1", "repo1", ITEM_TYPE_USER, false)

    @Before
    fun init() {
        dataBase = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun close() {
        dataBase.close()
    }

    @Test
    fun insertUserAndGet() = runBlockingTest {
        // When user is saved in db
        dataBase.usersDao().insertUser(user)
        // Then it can be retrieved by id
        val result = dataBase.usersDao().getUser(user.id)
        assertThat(result, `is`(user))
    }

    @Test
    fun getUsers_returnsOnePage() = runBlockingTest {
        // When db contains more that one page
        fillDb(PAGE_SIZE + 2)
        // Only one page is returned
        val result = dataBase.usersDao().getUsers(INITIAL_KEY, PAGE_SIZE)
        assertThat(result.size, `is`(PAGE_SIZE))
    }

    @Test
    fun getAllUsers_returnsAllPages() = runBlockingTest {
        // When db contains more that one page
        fillDb(PAGE_SIZE + 2)
        // Only one page is returned
        val result = dataBase.usersDao().getAllUsers(INITIAL_KEY)
        assertThat(result.size, `is`(PAGE_SIZE + 2))
    }

    @Test
    fun insertUserAndGetSync() = runBlockingTest {
        // When user is saved in db
        dataBase.usersDao().insertUserSync(user)
        // Then it can be retrieved by id
        val result = dataBase.usersDao().getUser(user.id)
        assertThat(result, `is`(user))
    }

    @Test
    fun getUsersSync_returnsOnePage() = runBlockingTest {
        // When db contains more that one page
        fillDb(PAGE_SIZE + 2)
        // Only one page is returned
        val result = dataBase.usersDao().getUsersSync(INITIAL_KEY, PAGE_SIZE)
        assertThat(result.size, `is`(PAGE_SIZE))
    }

    @Test
    fun getAllUsersSync_returnsAllPages() = runBlockingTest {
        // When db contains more that one page
        fillDb(PAGE_SIZE + 2)
        // Only one page is returned
        val result = dataBase.usersDao().getAllUsers(INITIAL_KEY)
        assertThat(result.size, `is`(PAGE_SIZE + 2))
    }

    @Test
    fun getAllUsersWithIdSmallerSync() = runBlockingTest {
        // When db contains more that one page
        fillDb(PAGE_SIZE + 2)
        // Only one page is returned
        val result = dataBase.usersDao().getAllUsersWithIdSmallerSync(10)
        for (dbUser in result) {
            assertThat(dbUser.id, `is`(lessThanOrEqualTo(10)))
        }

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