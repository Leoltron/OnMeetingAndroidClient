package ru.leoltron.onmeeting.ui.login

/**
 * Data validation state of the loginOrRegister form.
 */
class LoginFormState {
    var usernameError: Int? = null
        private set
    var passwordError: Int? = null
        private set
    var isDataValid: Boolean = false
        private set

    constructor(usernameError: Int?, passwordError: Int?) {
        this.usernameError = usernameError
        this.passwordError = passwordError
    }

    constructor(isDataValid: Boolean) {
        this.isDataValid = isDataValid
    }
}
