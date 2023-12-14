package org.ks.chan.mi.updater.ui.screen.login

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import org.ks.chan.mi.updater.storage.XiaomiCredential
import org.ks.chan.mi.updater.storage.xiaomiCredentialDataStore
import org.ks.chan.mi.updater.ui.screen.login.repository.XiaomiAccountAuthRepository
import org.ks.chan.mi.updater.ui.screen.login.repository.XiaomiAccountLoginRepository
import org.ks.chan.mi.updater.ui.screen.login.repository.XiaomiAccountSessionRepository
import org.ks.chan.mi.updater.util.EmptyStr
import org.ks.chan.mi.updater.util.coroutine.main

class LoginViewModel(
    private val xiaomiCredentialDataStore: DataStore<XiaomiCredential>,
): ViewModel() {

    companion object Factory {

        @Composable
        fun Context.loginViewModel(): LoginViewModel {
            return viewModel(
                factory = viewModelFactory {
                    initializer {
                        LoginViewModel(xiaomiCredentialDataStore)
                    }
                }
            )
        }

    }

    data class LoginCredential(
        val account: String = EmptyStr,
        val password: String = EmptyStr,
    )

    private val _loginCredential = MutableStateFlow(LoginCredential())
    val loginCredential: StateFlow<LoginCredential>
        get() = _loginCredential.asStateFlow()

    fun updateAccount(account: String) {
        _loginCredential.update { loginCredential ->
            loginCredential.copy(account = account)
        }
    }

    fun updatePassword(password: String) {
        _loginCredential.update { loginCredential ->
            loginCredential.copy(password = password)
        }
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState>
        get() = _uiState.asStateFlow()

    fun updateUiState(block: (LoginUiState) -> LoginUiState) {
        _uiState.update(block)
    }

    fun login(
        isLoading: Boolean = uiState.value.isLoading,
        account: String = loginCredential.value.account,
        password: String = loginCredential.value.password,
        isFieldsValid: Boolean = account.isNotBlank() && password.isNotBlank()
    ) {
        if (!isLoading && isFieldsValid) {
            _uiState.update { uiState ->
                uiState.copy(isLoading = true)
            }

            viewModelScope.main {
                val signPassport = getSignPassport()
                if (signPassport.isNullOrBlank()) {
                    return@main updateUiState {
                        LoginUiState(
                            isLoading = false,
                            loginError = LoginUiState.LoginError.ConnectionError
                        )
                    }
                }

                val authResponseBody = getAuthResponse(account, password, signPassport)
                    ?: return@main updateUiState {
                        LoginUiState(
                            isLoading = false,
                            loginError = LoginUiState.LoginError.ConnectionError
                        )
                    }

                if (!authResponseBody.isSuccess) {
                    return@main updateUiState { _ ->
                        LoginUiState(
                            isLoading = false,
                            loginError = LoginUiState.LoginError.AuthError
                        )
                    }
                }

                val serviceToken = getServiceToken(
                    authResponseBody.location, authResponseBody.security, authResponseBody.nonce
                ) ?: return@main updateUiState { _ ->
                    LoginUiState(
                        isLoading = false,
                        loginError = LoginUiState.LoginError.ServerError
                    )
                }

                xiaomiCredentialDataStore.updateData { xiaomiCredential ->
                    xiaomiCredential.toBuilder()
                        .setUserid(authResponseBody.userid)
                        .setSecurity(authResponseBody.security)
                        .setServiceToken(serviceToken)
                        .build()
                }

                updateUiState { LoginUiState(isCompleted = true) }
            }
        }
    }

    private suspend fun getSignPassport(): String? {
        return XiaomiAccountLoginRepository().signPassport
            .single()
    }

    private suspend fun getAuthResponse(
        account: String, password: String, signPassport: String
    ): XiaomiAccountAuthRepository.ResponseBody? {
        val repository = XiaomiAccountAuthRepository(
            account = account, password = password, signPassport = signPassport
        )
        return repository.auth.single()
    }

    private suspend fun getServiceToken(location: String, security: String, nonce: Long): String? {
        return XiaomiAccountSessionRepository(location, security, nonce)
            .serviceToken
            .single()
    }

}