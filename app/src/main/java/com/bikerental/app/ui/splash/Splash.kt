package com.bikerental.app.ui.splash

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bikerental.app.R

/**
 * Splash screen composable that handles:
 * - Displaying app branding/logo
 * - Preventing back button navigation
 * - Delegating auth flow logic to ViewModel
 *
 * @param modifier Modifier for styling/layout adjustments
 * @param viewModel Handles business logic and navigation decisions
 */
@Composable
fun Splash(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel
) {
    // Disable back button during splash screen
    BackHandler { viewModel.navigator.finish() }

    // Pure UI component without business logic
    SplashView(modifier)
}

/**
 * Stateless splash screen content that only handles UI rendering.
 * Shows centered app logo on a full-screen column.
 *
 * @param modifier Modifier for layout adjustments
 */
@Composable
private fun SplashView(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(96.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.image_logo_description)
        )
    }
}

/**
 * Preview function for design-time rendering of the splash screen.
 * Shows the splash screen UI without any ViewModel dependencies.
 */
@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    SplashView(modifier = Modifier)
}