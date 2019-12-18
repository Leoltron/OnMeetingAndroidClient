package ru.leoltron.onmeeting.data

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
open class Result<T>// hide the private constructor to limit subclass types (Success, Error)
private constructor() {

    override fun toString(): String {
        if (this is Result.Success<*>) {
            val success = this as Result.Success<*>
            return "Success[data=" + success.data.toString() + "]"
        } else if (this is Error) {
            val error = this as Error
            return "Error[exception=" + error.error.toString() + "]"
        }
        return ""
    }

    // Success sub-class
    class Success<T>(val data: T) : Result<T>()

    // Error sub-class
    class Error(val error: Exception) : Result<Any>()
}
