package com.reportedsocks.demoproject

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.manipulation.*
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.InitializationError
import java.lang.reflect.InvocationTargetException

/**
 * Well this is probably a dirty solution but this will use default JUnit4 runner
 * while AndroidJUnit4 will supply custom runner specified in build.gradle
 */
class MyAndroidTestRunner(klass: Class<*>) : Runner(),
    Filterable,
    Sortable {
    private val delegate: Runner
    override fun getDescription(): Description {
        return delegate.description
    }

    override fun run(runNotifier: RunNotifier) {
        delegate.run(runNotifier)
    }

    @Throws(NoTestsRemainException::class)
    override fun filter(filter: Filter) {
        (delegate as Filterable).filter(filter)
    }

    override fun sort(sorter: Sorter) {
        (delegate as Sortable).sort(sorter)
    }

    companion object {
        private const val TAG = "AndroidJUnit4"

        @Throws(InitializationError::class)
        private fun loadRunner(testClass: Class<*>): Runner {
            val runnerClassName = runnerClassName
            return loadRunner(testClass, runnerClassName)
        }

        // TODO: remove this logic when nitrogen is hooked up to always pass this property
        private val runnerClassName: String
            private get() = "androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner"

        @Throws(InitializationError::class)
        private fun loadRunner(testClass: Class<*>, className: String): Runner {
            try {
                val runnerClass =
                    Class.forName(className) as Class<out Runner>
                return runnerClass.getConstructor(Class::class.java)
                    .newInstance(testClass)
            } catch (e: ClassNotFoundException) {
                throwInitializationError(className, e)
            } catch (e: NoSuchMethodException) {
                throwInitializationError(className, e)
            } catch (e: IllegalAccessException) {
                throwInitializationError(className, e)
            } catch (e: InstantiationException) {
                throwInitializationError(className, e)
            } catch (e: InvocationTargetException) {
                throwInitializationError(className, e)
            }
            throw IllegalStateException("Should never reach here")
        }

        @Throws(InitializationError::class)
        private fun throwInitializationError(
            delegateRunner: String,
            cause: Throwable
        ) {
            // wrap the cause in a RuntimeException with a more detailed error message
            throw InitializationError(
                RuntimeException(String.format(
                    "Delegate runner '%s' for AndroidJUnit4 could not be loaded.", delegateRunner),
                    cause))
        }
    }

    init {
        delegate = loadRunner(klass)
    }
}