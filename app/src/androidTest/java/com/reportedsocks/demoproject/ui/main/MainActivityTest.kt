package com.reportedsocks.demoproject.ui.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.reportedsocks.demoproject.DaggerApplicationComponent
import com.reportedsocks.demoproject.MyAppTest
import com.reportedsocks.demoproject.di.modules.ContextModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

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


    @Test
    fun abc() {
        // just to see it running
        sleep(10000)
    }
}