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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikerental.app.R

@Composable
fun SignUp(
    modifier: Modifier,
    viewModel: SignUpViewModel
) {
    BackHandler { viewModel.navigator.finish() }

    SignUpView(
        modifier,
        name = viewModel.name.collectAsStateWithLifecycle().value,
        email = viewModel.email.collectAsStateWithLifecycle().value,
        password = viewModel.password.collectAsStateWithLifecycle().value,
        nameError = viewModel.nameError.collectAsStateWithLifecycle().value,
        emailError = viewModel.emailError.collectAsStateWithLifecycle().value,
        passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value,
        onNameChange = { viewModel.onNameChange(it) },
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        basicSignUp = { viewModel.basicSignUp() },
    )
}

@Composable
private fun SignUpView(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    password: String,
    nameError: String,
    emailError: String,
    passwordError: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    basicSignUp: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f)
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
                Image(
                    painterResource(R.drawable.bike),
                    contentDescription = "",
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Username") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Rocket,
                            contentDescription = "Username"
                        )
                    },
                    isError = nameError.isNotEmpty(),
                    supportingText = {
                        Text(text = emailError)
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = "Email"
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Password,
                            contentDescription = "Password"
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
                    onClick = basicSignUp,
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}