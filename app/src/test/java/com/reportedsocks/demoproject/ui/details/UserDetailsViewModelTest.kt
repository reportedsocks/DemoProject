package com.reportedsocks.demoproject.ui.details

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.whenever
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.data.source.DefaultDataRepository
import com.reportedsocks.demoproject.getOrAwaitValue
import com.reportedsocks.demoproject.ui.util.ITEM_TYPE_USER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UserDetailsViewModelTest {
    @Mock
    private lateinit var dataRepository: DefaultDataRepository
    private lateinit var viewModel: UserDetailsViewModel

    private val user = User(1, "login1", "avatar1", "html1", "repo1", ITEM_TYPE_USER, false)

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        viewModel = UserDetailsViewModel(dataRepository)
    }

    @Test
    fun start_successReturned() = runBlockingTest {
        // Given repo returns User
        whenever(dataRepository.getUser(1)).thenReturn(Result.Success(user))
        // When it is requested in viewmodel
        viewModel.start(1)
        // Then liveData is updated
        val result = viewModel.userItem.getOrAwaitValue()
        assertThat(result, `is`(user))
    }

    @Test
    fun start_errorReturned() = runBlockingTest {
        // Given repo returns User
        whenever(dataRepository.getUser(1)).thenReturn(Result.Error(Exception()))
        // When it is requested in viewmodel
        viewModel.start(1)
        // Then liveData is updated
        val result = viewModel.snackbarText.getOrAwaitValue().peekContent()
        assertThat(result, `is`(R.string.error_loading_user))
    }

    @Test
    fun openBrowser_createsCorrectEvent() = runBlockingTest {
        // Given repo returns User and it was called
        whenever(dataRepository.getUser(1)).thenReturn(Result.Success(user))
        viewModel.start(1)
        // When openBrowser is called
        viewModel.openBrowser()
        // Then url in Event is htmlUrl of the user
        val url = viewModel.browserRedirectEvent.getOrAwaitValue().peekContent()
        assertThat(url, `is`(user.htmlUrl))
    }
}