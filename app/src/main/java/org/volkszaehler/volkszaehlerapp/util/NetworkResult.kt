package org.volkszaehler.volkszaehlerapp.util

/**
 * Wrapper-Klasse für Netzwerk-Operationen
 * Enthält drei Zustände: Success, Error, Loading
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error
}