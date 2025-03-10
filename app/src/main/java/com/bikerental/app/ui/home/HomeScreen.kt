package com.bikerental.app.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.bikerental.app.ui.theme.BikeRentalTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreenContent()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenContent() {
    Text("test")
}

@Composable
@Preview(showSystemUi = true)
fun HomeScreenPreview() {
    BikeRentalTheme(darkTheme = true) {
        HomeScreenContent()
    }
}