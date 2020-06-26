package com.reportedsocks.demoproject

import com.reportedsocks.demoproject.data.source.local.LocalDataSourceTest
import com.reportedsocks.demoproject.data.source.local.UsersDaoTest
import com.reportedsocks.demoproject.ui.details.UserDetailsFragmentTest
import com.reportedsocks.demoproject.ui.main.MainFragmentTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    LocalDataSourceTest::class,
    UsersDaoTest::class,
    UserDetailsFragmentTest::class,
    MainFragmentTest::class
)
class TestSuite