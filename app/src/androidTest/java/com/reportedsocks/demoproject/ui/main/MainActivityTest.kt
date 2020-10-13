package com.reportedsocks.demoproject.ui.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.reportedsocks.demoproject.DaggerApplicationComponent
import com.reportedsocks.demoproject.MyAppTest
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.di.modules.ContextModule
import com.reportedsocks.demoproject.util.*
import junit.framework.Assert.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val rule = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
        override fun beforeActivityLaunched() {
            // swap out DaggerTestApplicationComponent for the real one
            val app = ApplicationProvider.getApplicationContext() as MyAppTest
            app.appComponent = DaggerApplicationComponent.builder()
                .contextModule(ContextModule(app)).build()
        }
    }
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @Test
    fun clickOnUser_DetailsFragmentOpens() {
        dataBindingIdlingResource.monitorActivity(rule.activity)
        // Given that main fragment is opened
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.app_name))))
        // When click on first user
        onView(withText("Id: 1")).check(matches(isDisplayed()))
        onView(withText("Id: 1")).perform(click())
        // Then userDetails fragment is opened
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.user_details_fragment))))
        onView(withText("1")).check(matches(isDisplayed()))
    }

    @Test
    fun checkFilter_ALL() {
        dataBindingIdlingResource.monitorActivity(rule.activity)
        // Given - filter ALL is selected
        onView(withText(R.string.label_all)).check(matches(isDisplayed()))
        // When two pages are loaded
        onView(withId(R.id.users_list)).perform(actionOnItemAtPosition<UsersAdapter.ViewHolder>(30,
            scrollTo()))
        onView(withId(R.id.users_list)).perform(actionOnItemAtPosition<UsersAdapter.ViewHolder>(60,
            scrollTo()))
        // Then there is at least one Organisation and one User on loaded pages
        var hasFoundOrganisation = false
        var userPosition = 0
        for (position in 0..60) {
            try {
                RecyclerViewMatcher(R.id.users_list).atPosition(position)
                    .matches(hasDescendant(withText(
                        ITEM_TYPE_ORGANISATION)))
                hasFoundOrganisation = true
            } catch (e: Exception) {
                userPosition = position
            }
        }
        assertThat(hasFoundOrganisation, `is`(true))
        RecyclerViewMatcher(R.id.users_list).atPosition(userPosition)
            .matches(hasDescendant(withText(
                ITEM_TYPE_USER)))
    }

    @Test
    fun checkFilter_Organisation() {
        dataBindingIdlingResource.monitorActivity(rule.activity)
        // Given two pages were loaded
        onView(withText(R.string.label_all)).check(matches(isDisplayed()))
        onView(withId(R.id.users_list)).perform(actionOnItemAtPosition<UsersAdapter.ViewHolder>(30,
            scrollTo()))
        onView(withId(R.id.users_list)).perform(actionOnItemAtPosition<UsersAdapter.ViewHolder>(60,
            scrollTo()))
        // When organisations filter selected
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.organisation)).perform(click())
        // Then at least one Organisation is present and no Users
        onView(withText(R.string.label_organisations)).check(matches(isDisplayed()))
        RecyclerViewMatcher(R.id.users_list).atPosition(0).matches(hasDescendant(withText(
            ITEM_TYPE_ORGANISATION)))
        onView(withId(R.id.users_list)).check(matches(not(hasDescendant(withText(ITEM_TYPE_USER)))))
    }

    @Test
    fun checkFilter_User() {
        dataBindingIdlingResource.monitorActivity(rule.activity)
        // Given two pages were loaded
        onView(withText(R.string.label_all)).check(matches(isDisplayed()))
        onView(withId(R.id.users_list)).perform(actionOnItemAtPosition<UsersAdapter.ViewHolder>(30,
            scrollTo()))
        onView(withId(R.id.users_list)).perform(actionOnItemAtPosition<UsersAdapter.ViewHolder>(60,
            scrollTo()))
        // When users filter selected
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.user)).perform(click())
        // Then at least one User is present and no Organisations
        onView(withText(R.string.label_users)).check(matches(isDisplayed()))
        RecyclerViewMatcher(R.id.users_list).atPosition(0).matches(hasDescendant(withText(
            ITEM_TYPE_USER)))
        onView(withId(R.id.users_list)).check(matches(not(hasDescendant(withText(
            ITEM_TYPE_ORGANISATION)))))
    }
}