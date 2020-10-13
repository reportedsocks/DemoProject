package com.reportedsocks.demoproject.ui.main

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.source.DefaultDataRepository
import com.reportedsocks.demoproject.getOrAwaitValue
import junit.framework.Assert.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.atMost
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MainViewModelTest {

    @Mock
    private lateinit var dataRepository: DefaultDataRepository
    private lateinit var viewModel: MainViewModel

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        viewModel = MainViewModel(dataRepository)
    }

    @Test
    fun setFiltering_organisation_updatesAllValues() {
        // When filter is applied
        viewModel.setFiltering(UsersFilterType.ORGANISATION)
        // Then filter in dataRepository is updated an filter params for ui are also updated
        verify(dataRepository).currentFiltering = UsersFilterType.ORGANISATION

        val currentFilteringValue = viewModel.currentFilteringLabel.getOrAwaitValue()
        assertThat(currentFilteringValue, `is`(R.string.label_organisations))

        val noItemsFilteringLabel = viewModel.noItemsFilteringLabel.getOrAwaitValue()
        assertThat(noItemsFilteringLabel, `is`(R.string.no_items_organisations))

        val noItemsIconRes = viewModel.noItemsIconRes.getOrAwaitValue()
        assertThat(noItemsIconRes, `is`(R.drawable.ic_baseline_people_outline_24))
    }

    @Test
    fun setFiltering_user_updatesAllValues() {
        // When filter is applied
        viewModel.setFiltering(UsersFilterType.USER)
        // Then filter in dataRepository is updated an filter params for ui are also updated
        verify(dataRepository).currentFiltering = UsersFilterType.USER

        val currentFilteringValue = viewModel.currentFilteringLabel.getOrAwaitValue()
        assertThat(currentFilteringValue, `is`(R.string.label_users))

        val noItemsFilteringLabel = viewModel.noItemsFilteringLabel.getOrAwaitValue()
        assertThat(noItemsFilteringLabel, `is`(R.string.no_items_users))

        val noItemsIconRes = viewModel.noItemsIconRes.getOrAwaitValue()
        assertThat(noItemsIconRes, `is`(R.drawable.ic_baseline_person_outline_24))
    }

    @Test
    fun setFiltering_all_updatesAllValues() {
        // When filter is applied
        viewModel.setFiltering(UsersFilterType.ALL)
        // Then filter in dataRepository is updated an filter params for ui are also updated
        verify(dataRepository, atMost(2)).currentFiltering = UsersFilterType.ALL

        val currentFilteringValue = viewModel.currentFilteringLabel.getOrAwaitValue()
        assertThat(currentFilteringValue, `is`(R.string.label_all))

        val noItemsFilteringLabel = viewModel.noItemsFilteringLabel.getOrAwaitValue()
        assertThat(noItemsFilteringLabel, `is`(R.string.no_items_all))

        val noItemsIconRes = viewModel.noItemsIconRes.getOrAwaitValue()
        assertThat(noItemsIconRes, `is`(R.drawable.ic_baseline_not_interested_24))
    }

    @Test
    fun showError_networkException_createsCorrectEvent() {
        // When network exception is passed
        viewModel.showError(Result.Error(Exception(), true))
        val message = viewModel.snackbarText.getOrAwaitValue().peekContent()
        // Then Event contains corresponding string
        assertThat(message, `is`(R.string.error_updating_items))
    }

    @Test
    fun showError_localException_createsCorrectEvent() {
        // When network exception is passed
        viewModel.showError(Result.Error(Exception(), false))
        val message = viewModel.snackbarText.getOrAwaitValue().peekContent()
        // Then Event contains corresponding string
        assertThat(message, `is`(R.string.error_loading_items))
    }

    @Test
    fun openUser_createsCorrectEvent() {
        // When id is passed
        viewModel.openUser(1)
        // Event contains same id
        val id = viewModel.openUserEvent.getOrAwaitValue().peekContent()
        assertThat(id, `is`(1))
    }

}