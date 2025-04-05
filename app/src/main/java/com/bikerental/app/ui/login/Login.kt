package com.bikerental.app.ui.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikerental.app.R

/**
 * Composable function that displays the login screen.
 *
 * Handles the UI elements for login, including email, password input fields,
 * and login-related actions (like basic login, switching to sign-up, and password reset).
 *
 * @param modifier The modifier to be applied to the root view.
 * @param viewModel The ViewModel responsible for managing the login data and actions.
 */
@Composable
fun Login(
    modifier: Modifier,
    viewModel: LoginViewModel
) {
    // Handles back press to navigate away from login screen
    BackHandler { viewModel.navigator.finish() }

    // Displays the login view with current data from the ViewModel
    LoginView(
        modifier,
        email = viewModel.email.collectAsStateWithLifecycle().value,
        password = viewModel.password.collectAsStateWithLifecycle().value,
        emailError = viewModel.emailError.collectAsStateWithLifecycle().value,
        passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value,
        loginError = viewModel.loginError.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        basicLogin = { viewModel.basicLogin() },
        switchSignUp = { viewModel.switchSignUp() },
        resetPassword = { viewModel.resetPassword() }
    )
}

/**
 * Composable function that renders the UI components of the login screen.
 *
 * Displays input fields for email and password, error messages, and buttons for login,
 * sign-up switch, and password reset.
 *
 * @param modifier The modifier to be applied to the root view.
 * @param email The current email entered by the user.
 * @param password The current password entered by the user.
 * @param emailError Error message related to the email input field.
 * @param passwordError Error message related to the password input field.
 * @param loginError Error message for login-related issues.
 * @param isLoading Boolean indicating if the login request is in progress.
 * @param onEmailChange Function to handle email input changes.
 * @param onPasswordChange Function to handle password input changes.
 * @param basicLogin Function to trigger the login action.
 * @param switchSignUp Function to trigger switching to the sign-up screen.
 * @param resetPassword Function to trigger the password reset screen.
 */
@Composable
private fun LoginView(
    modifier: Modifier,
    email: String,
    password: String,
    emailError: String,
    passwordError: String,
    loginError: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    basicLogin: () -> Unit,
    switchSignUp: () -> Unit,
    resetPassword: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .verticalScroll(rememberScrollState())
                .defaultMinSize()
                .align(Alignment.Center)
                .background(
                    color = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display the logo at the top
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                Image(
                    painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.image_logo_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )
            }

            // Email input field
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = stringResource(R.string.email_icon_description)
                        )
                    },
                    isError = emailError.isNotEmpty(),
                    supportingText = {
                        Text(text = emailError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                )
            }

            // Password input field
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Password,
                            contentDescription = stringResource(R.string.password_icon_description)
                        )
                    },
                    isError = passwordError.isNotEmpty(),
                    supportingText = {
                        Text(text = passwordError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            // Display login error message
            if (loginError.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                ) {
                    Text(
                        text = loginError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Login button
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 48.dp
                )
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = basicLogin,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(R.string.login),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Switch to sign-up text
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 48.dp
                ).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.clickable {
                        switchSignUp()
                    },
                    text = stringResource(R.string.sign_up_text),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Reset password text
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 48.dp
                ).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.clickable {
                        resetPassword()
                    },
                    text = stringResource(R.string.forgot_password),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
