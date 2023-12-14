package org.ks.chan.mi.updater.ui.screen.login

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import org.ks.chan.mi.updater.R
import org.ks.chan.mi.updater.ui.screen.login.LoginViewModel.Factory.loginViewModel
import org.ks.chan.mi.updater.ui.screen.login.LoginViewModel.LoginCredential
import org.ks.chan.mi.updater.ui.theme.dimension.Sizing_72
import org.ks.chan.mi.updater.ui.theme.dimension.Spacing_16
import org.ks.chan.mi.updater.ui.theme.dimension.Spacing_8
import org.ks.chan.mi.updater.util.EmptyStr
import org.ks.chan.mi.updater.util.coroutine.main
import org.ks.chan.mi.updater.util.state.falseState

@Composable
fun Login(
    exit: () -> Unit,
    complete: () -> Unit,
    context: Context = LocalContext.current,
    loginViewModel: LoginViewModel = context.loginViewModel()
) {
    val uiState by loginViewModel.uiState
        .collectAsStateWithLifecycle()
    val loginCredential by loginViewModel.loginCredential
        .collectAsStateWithLifecycle()

    LoginContent(
        exit = exit,
        complete = complete,
        uiState = uiState,
        updateUiState = { loginViewModel.updateUiState { it } },
        login = loginViewModel::login,
        loginCredential = loginCredential,
        onAccountChange = loginViewModel::updateAccount,
        onPasswordChange = loginViewModel::updatePassword
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent(
    exit: () -> Unit,
    complete: () -> Unit,
    login: () -> Unit = {},
    uiState: LoginUiState,
    updateUiState: (LoginUiState) -> Unit,
    loginCredential: LoginCredential,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val lifecycleOwner = LocalLifecycleOwner.current
    if (uiState.loginError != LoginUiState.LoginError.None) {
        val message = stringResource(
            id = when (uiState.loginError) {
                LoginUiState.LoginError.ConnectionError -> R.string.login_snack_error_connection
                LoginUiState.LoginError.AuthError -> R.string.login_snack_error_auth
                LoginUiState.LoginError.ServerError -> R.string.login_snack_error_server
                else -> return
            }
        )
        lifecycleOwner.lifecycleScope.main {
            snackbarHostState.showSnackbar(message = message)
            updateUiState(uiState.copy(loginError = LoginUiState.LoginError.None))
        }
    }
    if (uiState.isCompleted) {
        val message = stringResource(id = R.string.login_snack_complete)
        lifecycleOwner.lifecycleScope.main {
            snackbarHostState.showSnackbar(message = message)
            complete()
        }
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = exit) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(id = R.string.action_exit)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.login_title))
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2F)
            ) {

                val image = createRef()

                Image(
                    modifier = Modifier
                        .size(Sizing_72)
                        .constrainAs(image) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_mi_24dp),
                    contentDescription = stringResource(id = R.string.login_icon_xiaomi)
                )

            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing_16),
                value = loginCredential.account,
                onValueChange = { newText ->
                    newText.lineSequence()
                        .first()
                        .let { text ->
                            if (text != loginCredential.account) {
                                onAccountChange(text)
                            }
                        }
                },
                singleLine = true,
                enabled = !uiState.isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = stringResource(
                            id = R.string.login_field_account_leading
                        )
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.login_field_account_label))
                }
            )

            var passwordVisibility by remember { falseState }
            var passwordFocused by remember { falseState }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing_16, vertical = Spacing_8)
                    .onFocusChanged { passwordFocused = it.isFocused },
                value = loginCredential.password,
                onValueChange = { newText ->
                    newText.lineSequence()
                        .first()
                        .let { text ->
                            if (text != loginCredential.password) {
                                onPasswordChange(text)
                            }
                        }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = when {
                    passwordVisibility -> VisualTransformation.None
                    else -> PasswordVisualTransformation()
                },
                singleLine = true,
                enabled = !uiState.isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = stringResource(
                            id = R.string.login_field_password_leading
                        )
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.login_field_password_label))
                },
                trailingIcon = {
                    Crossfade(targetState = passwordFocused, label = EmptyStr) { isFocused ->
                        if (isFocused) {
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Crossfade(targetState = passwordVisibility, label = EmptyStr) { isVisible ->
                                    Icon(
                                        imageVector = when {
                                            isVisible -> Icons.Rounded.VisibilityOff
                                            else -> Icons.Rounded.Visibility
                                        },
                                        contentDescription = stringResource(id = when {
                                            isVisible -> R.string.action_password_invisible
                                            else -> R.string.action_password_visible
                                        })
                                    )
                                }
                            }
                        }
                    }
                }
            )

            Crossfade(targetState = uiState.isLoading, label = EmptyStr) { isLoading ->
                when {
                    isLoading -> {
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing_16),
                            onClick = { /*TODO*/ },
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(ButtonDefaults.IconSize)
                            )
                        }
                    }
                    else -> {
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing_16),
                            onClick = login
                        ) {
                            Text(text = stringResource(id = R.string.login_button_login))
                        }
                    }
                }
                
            }

            Row(
                modifier = Modifier.padding(horizontal = Spacing_16)
            ) {

                Spacer(modifier = Modifier.weight(1F))

                TextButton(
                    onClick = { /*TODO*/ }
                ) {
                    Text(text = stringResource(id = R.string.login_button_anonymous))
                }

            }

        }

    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview(
    exit: () -> Unit = {},
    complete: () -> Unit = {},
) {
    var uiState by remember {
        mutableStateOf(LoginUiState())
    }
    var loginCredential by remember {
        mutableStateOf(LoginCredential())
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LoginContent(
        exit = exit,
        complete = complete,
        login = {
            lifecycleOwner.lifecycleScope.main {
                uiState = uiState.copy(isLoading = true)
                delay(2000)
                uiState = uiState.copy(isLoading = false)
            }
        },
        uiState = uiState,
        updateUiState = { uiState = it },
        loginCredential = loginCredential,
        onAccountChange = {
            loginCredential = loginCredential.copy(account = it)
        },
        onPasswordChange = {
            loginCredential = loginCredential.copy(password = it)
        }
    )
}