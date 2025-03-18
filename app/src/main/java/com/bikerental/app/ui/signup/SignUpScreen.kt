package com.bikerental.app.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikerental.app.data.model.ErrorMessage
import com.bikerental.app.ui.theme.BikeRentalTheme

@Composable
fun SignUpScreen(
    openHomeScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val shouldRestartApp by viewModel.shouldRestartApp.collectAsStateWithLifecycle()

    if (shouldRestartApp) {
        openHomeScreen()
    } else {
        SignUpScreenContent(
            signUp = viewModel::signUp,
            showErrorSnackbar = showErrorSnackbar
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignUpScreenContent(
    signUp: (String, String, String, (ErrorMessage) -> Unit) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Repeat Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        errorMessage?.let {
            Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(4.dp))
        }

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                    errorMessage = "All fields are required!"
                } else {
                    errorMessage = null
                    signUp(
                            email,
                            password,
                            repeatPassword,
                            showErrorSnackbar
                        )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Sign Up")
        }
    }
}




//@Composable
//@OptIn(ExperimentalMaterial3Api::class)
//fun SignUpScreenContent(
//    signUp: (String, String, String, (ErrorMessage) -> Unit) -> Unit,
//    showErrorSnackbar: (ErrorMessage) -> Unit
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var repeatPassword by remember { mutableStateOf("") }
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
//
//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
//    ) { innerPadding ->
//        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
//            val (appLogo, form) = createRefs()
//
//            Column(
//                modifier = Modifier
//                    .constrainAs(appLogo) {
//                        top.linkTo(parent.top)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                    },
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Spacer(Modifier.size(24.dp))
//
//                Image(
//                    modifier = Modifier.size(88.dp),
//                    painter = painterResource(id = R.mipmap.ic_launcher_round),
//                    contentDescription = "App logo"
//                )
//
//                Spacer(Modifier.size(24.dp))
//            }
//
//            Column(
//                modifier = Modifier
//                    .constrainAs(form) {
//                        top.linkTo(parent.top)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                    },
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Spacer(Modifier.size(24.dp))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    value = email,
//                    onValueChange = { email = it },
//                    label = { Text(stringResource(R.string.email)) }
//                )
//
//                Spacer(Modifier.size(16.dp))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text(stringResource(R.string.password)) },
//                    visualTransformation = PasswordVisualTransformation()
//                )
//
//                Spacer(Modifier.size(16.dp))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    value = repeatPassword,
//                    onValueChange = { repeatPassword = it },
//                    label = { Text(stringResource(R.string.repeat_password)) },
//                    visualTransformation = PasswordVisualTransformation()
//                )
//
//                Spacer(Modifier.size(32.dp))
//
//                StandardButton(
//                    label = R.string.sign_up_with_email,
//                    onButtonClick = {
//                        signUp(
//                            email,
//                            password,
//                            repeatPassword,
//                            showErrorSnackbar
//                        )
//                    }
//                )
//                Spacer(Modifier.size(16.dp))
//            }
//        }
//    }
//}

@Composable
@Preview(showSystemUi = true)
fun SignUpScreenPreview() {
    BikeRentalTheme(darkTheme = true) {
        SignUpScreenContent(
            signUp = { _, _, _, _ -> },
            showErrorSnackbar = {}
        )
    }
}