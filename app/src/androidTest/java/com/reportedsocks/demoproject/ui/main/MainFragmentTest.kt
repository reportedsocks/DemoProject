package com.reportedsocks.demoproject.ui.main

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.reportedsocks.demoproject.R
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@MediumTest
@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @Test
    fun clickOnUser_NavigateToUserDetails() {
        // Given - on main fragment
        val scenario = launchFragmentInContainer<MainFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // When clicked on user1
        onView(withText("login1")).perform(click())
        // Then navigate to user details fragment with id 1
        verify(navController).navigate(
            MainFragmentDirections.actionMainFragmentToUserDetailsFragment(1)
        )
    }
}