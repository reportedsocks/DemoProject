package com.reportedsocks.demoproject.data

/**
 * Represents result of the operation on DataSource with 3 corresponding states:
 * Success - contains return value
 * Error - contains Exception and marker whether it was thrown due to network issues
 * Loading
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : com.reportedsocks.demoproject.data.Result<T>()

    data class Error(val exception: Exception, var isNetworkException: Boolean = true) :
        com.reportedsocks.demoproject.data.Result<Nothing>()

    object Loading : com.reportedsocks.demoproject.data.Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data = $data]"
            is Error -> "Error[exception = $exception]"
            is Loading -> "Loading"
        }
    }


}
