package com.reportedsocks.demoproject.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.reportedsocks.demoproject.MyAppTest

/**
 * Will create MyAppTest application class with test dagger component
 */
class MyTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MyAppTest::class.java.name, context)
    }
}