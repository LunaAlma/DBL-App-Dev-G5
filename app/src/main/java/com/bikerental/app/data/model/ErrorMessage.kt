package com.bikerental.app.data.model

import androidx.annotation.StringRes

/**
 * Sealed class representing different types of error messages.
 *
 * This class is used to represent error messages in the application, allowing both
 * string-based error messages and resource-based error messages (using string resource IDs).
 * It is sealed to ensure that all possible error message types are known at compile-time.
 *
 * Subclasses:
 * - [StringError]: Represents an error message as a string.
 * - [IdError]: Represents an error message using a string resource ID.
 */
sealed class ErrorMessage {

    /**
     * Represents an error message as a plain string.
     *
     * @property message The error message as a string.
     */
    class StringError(val message: String) : ErrorMessage()

    /**
     * Represents an error message using a string resource ID.
     *
     * @property message The string resource ID of the error message.
     */
    class IdError(@StringRes val message: Int) : ErrorMessage()
}
