package com.reportedsocks.demoproject

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Will create MyAppTest application class with test dagger component
 */
class MyTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MyAppTest::class.java.name, context)
    }
}