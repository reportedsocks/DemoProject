package com.reportedsocks.demoproject.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.reportedsocks.demoproject.MainCoroutineRule
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.getOrAwaitValue
import com.reportedsocks.demoproject.ui.main.UsersFilterType
import com.reportedsocks.demoproject.ui.util.INITIAL_KEY
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_ORGANISATION
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_USER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultDataRepositoryTest {

    private val user1 =
        User(1, "login1", "avatar1", "html1", "repo1", ITEM_TYPE_USER, false)
    private val user2 =
        User(2, "login2", "avatar2", "html2", "repo2", ITEM_TYPE_ORGANISATION, false)
    private val user3 =
        User(3, "login3", "avatar3", "html3", "repo3", ITEM_TYPE_ORGANISATION, false)
    private val user4 =
        User(4, "login4", "avatar4", "html4", "repo4", ITEM_TYPE_USER, false)

    private val users = listOf(user1, user2, user3, user4)
    private val usersStart = listOf(user1, user2)
    private val usersEnd = listOf(user3, user4)
    private lateinit var remoteDataSource: FakeDataSource
    private lateinit var localDataSource: FakeDataSource
    private lateinit var dataRepository: DefaultDataRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        remoteDataSource = FakeDataSource(users)
        localDataSource = FakeDataSource(emptyList())
        dataRepository = DefaultDataRepository((remoteDataSource as DataSource),
            (localDataSource as DataSource),
            true)
    }

    @Test
    fun loadAndSaveUsers_pagesReturned() = mainCoroutineRule.runBlockingTest {
        // When page is queried ( page limited to 2 items)
        val firstPage = dataRepository.loadAndSaveUsers(INITIAL_KEY)
        val secondPage = dataRepository.loadAndSaveUsers(firstPage.last().id)
        // Then page contents are correct
        assertThat(firstPage, IsEqual(usersStart))
        assertThat(secondPage, IsEqual(usersEnd))
    }

    @Test
    fun loadAndSaveUsers_filteringTypeUsers() = mainCoroutineRule.runBlockingTest {
        // Given users type filter applied
        dataRepository.currentFiltering = UsersFilterType.USER
        // When page is queried ( page limited to 2 items)
        val firstPage = dataRepository.loadAndSaveUsers(INITIAL_KEY)
        // it would query empty page here with firstPage.last().id, this logic is covered in UserBoundaryCallback
        val secondPage = dataRepository.loadAndSaveUsers(user2.id)
        // Then page contents are correct
        assertThat(firstPage, IsEqual(listOf(user1)))
        assertThat(secondPage, IsEqual(listOf(user4)))
    }

    @Test
    fun loadAndSaveUsers_isEmptyUpdated() = mainCoroutineRule.runBlockingTest {
        // Given users type filter applied
        dataRepository.currentFiltering = UsersFilterType.USER
        // When page with non-users is queried ( page limited to 2 items)
        dataRepository.loadAndSaveUsers(1)
        // Then LiveData is updated
        val isEmpty = dataRepository.isEmpty.getOrAwaitValue()
        assertThat(isEmpty, IsEqual(true))
    }

    @Test
    fun peekUsersIfAvailable_checkThroughAllDB() = mainCoroutineRule.runBlockingTest {
        // Given both pages have been loaded and organisations type filter applied
        val firstPage = dataRepository.loadAndSaveUsers(INITIAL_KEY)
        dataRepository.loadAndSaveUsers(firstPage.last().id)
        dataRepository.currentFiltering = UsersFilterType.ORGANISATION
        // When peekUsersIfAvailable called
        val result = dataRepository.peekUsersIfAvailable()
        // Then only organisations are returned from all pages
        assertThat(result, IsEqual(listOf(user2, user3)))
    }


    @Test
    fun loadInitialUsers_loadsItemsBeforeAndPageAfter() {
        // Given both pages have been loaded
        mainCoroutineRule.runBlockingTest {
            val firstPage = dataRepository.loadAndSaveUsers(INITIAL_KEY)
            dataRepository.loadAndSaveUsers(firstPage.last().id)
        }
        // When loadInitialUsers is called with id of second item
        val result = dataRepository.loadInitialUsers(user2.id)
        // Then all items before it and one page after is returned
        assertThat(result, IsEqual(users))
    }

    @Test
    fun loadInitialUsers_calledUserBoundaryCallback_loadsAllItems() {
        // Given both pages have been loaded and calledUserBoundaryCallback
        mainCoroutineRule.runBlockingTest {
            val firstPage = dataRepository.loadAndSaveUsers(INITIAL_KEY)
            dataRepository.loadAndSaveUsers(firstPage.last().id)
        }
        dataRepository.boundaryCallbackWasCalled = true
        // When loadInitialUsers is called
        val result = dataRepository.loadInitialUsers(INITIAL_KEY)
        // Then all items returned
        assertThat(result, IsEqual(users))
    }

    @Test
    fun getUser_returnsRequestedUser() = mainCoroutineRule.runBlockingTest {
        // Given both pages have been loaded
        val firstPage = dataRepository.loadAndSaveUsers(INITIAL_KEY)
        dataRepository.loadAndSaveUsers(firstPage.last().id)
        // When getUser is called
        val result = dataRepository.getUser(user3.id)
        // Then corresponding user is returned
        assertThat(result, IsInstanceOf(Result.Success::class.java))
        val data = (result as Result.Success).data
        assertThat(data, IsEqual(user3))
    }
}