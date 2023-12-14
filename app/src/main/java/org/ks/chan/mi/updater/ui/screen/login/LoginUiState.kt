package org.ks.chan.mi.updater.ui.screen.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val loginError: LoginError = LoginError.None
) {

    enum class LoginError {
        None,
        ConnectionError,
        AuthError,
        ServerError,
    }

}