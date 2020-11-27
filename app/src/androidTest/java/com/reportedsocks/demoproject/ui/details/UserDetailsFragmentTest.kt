package com.reportedsocks.demoproject.ui.details

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*@MediumTest
@RunWith(AndroidJUnit4::class)
class UserDetailsFragmentTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)*/

    /*@Test
    fun userIsDisplayed() {
        // When fragment is launched
        val bundle = Bundle().apply {
            putInt("userId", 1)
        }
        val scenario = launchFragmentInContainer<UserDetailsFragment>(bundle, R.style.AppTheme)
        // Then user is displayed in ui
        onView(withText("login1")).check(matches(isDisplayed()))
        onView(withText("1")).check(matches(isDisplayed()))
        onView(withText("User")).check(matches(isDisplayed()))
        onView(withText("False")).check(matches(isDisplayed()))
    }

    @Test
    fun intentSentToBrowser() {
        // Given fragment is launched
        val bundle = Bundle().apply {
            putInt("userId", 1)
        }
        val scenario = launchFragmentInContainer<UserDetailsFragment>(bundle, R.style.AppTheme)
        // When redirect button is clicked
        onView(withId(R.id.user_details_web_button)).perform(click())
        // Then intent is sent
        intended(anyIntent())
    }*/
//}