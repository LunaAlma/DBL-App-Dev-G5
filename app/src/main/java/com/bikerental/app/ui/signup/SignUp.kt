package com.bikerental.app.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikerental.app.R

/**
 * Composable function for the Sign Up screen.
 *
 * Responsibilities:
 * - Displays sign-up form with name, email, and password fields
 * - Shows validation errors and loading states
 * - Handles back button press via ViewModel
 * - Delegates user actions to ViewModel
 *
 * @param modifier Modifier for layout adjustments
 * @param viewModel Handles business logic and state management
 */
@Composable
fun SignUp(
    modifier: Modifier,
    viewModel: SignUpViewModel
) {
    // Prevent back navigation during sign-up process
    BackHandler { viewModel.navigator.finish() }

    SignUpView(
        modifier,
        name = viewModel.name.collectAsStateWithLifecycle().value,
        email = viewModel.email.collectAsStateWithLifecycle().value,
        password = viewModel.password.collectAsStateWithLifecycle().value,
        nameError = viewModel.nameError.collectAsStateWithLifecycle().value,
        emailError = viewModel.emailError.collectAsStateWithLifecycle().value,
        passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value,
        signUpError = viewModel.signUpError.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onNameChange = { viewModel.onNameChange(it) },
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        basicSignUp = { viewModel.basicSignUp() },
        switchLogin = { viewModel.switchLogin() }
    )
}

/**
 * Stateless UI component for the Sign Up screen.
 *
 * @param name Current value of name field
 * @param onNameChange Callback for name field changes
 * @param isLoading Loading state indicator
 * @param basicSignUp Callback for sign-up button press
 * @param switchLogin Callback for "Already have an account" link
 */
@Composable
private fun SignUpView(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    password: String,
    nameError: String,
    emailError: String,
    passwordError: String,
    signUpError: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    basicSignUp: () -> Unit,
    switchLogin: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            // Centered form container with scrollable content
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
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                // Application Logo
                Image(
                    painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.image_logo_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 4.dp
                )
            ) {
                // Name input field
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.name)
                        )
                    },
                    isError = nameError.isNotEmpty(),
                    supportingText = {
                        Text(text = nameError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                )
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
                // Email input field
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = stringResource(R.string.email)
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
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
                // Password input field
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Password,
                            contentDescription = stringResource(R.string.password)
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
            if (signUpError.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                ) {
                    Text(
                        text = signUpError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 48.dp
                )
            ) {
                // Sign Up button with loading state
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = basicSignUp,
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
                            text = stringResource(R.string.sign_up),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 48.dp
                ).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Switch to Login
                Text(
                    modifier = Modifier.clickable {
                        switchLogin()
                    },
                    text = stringResource(R.string.login_text),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}