package com.bikerental.app.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import com.bikerental.app.ui.maps.SearchBar

//@Composable
//fun Search(
//    modifier: Modifier,
//    viewModel: SearchViewModel
//) {
//    SearchView()
//}
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun SearchView() {
//
//    Scaffold(
//        topBar = {
//            SearchBar(
//                text = "searchText",
//                onTextChange = { "searchText = it" },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                onClose = {}
//            )
//        },
//        modifier = Modifier.fillMaxSize().systemBarsPadding(),
//    ) {
//        LazyColumn {
//            items(100) {
//                Text(
//                    text = "I'm item $it",
//                    modifier = Modifier.fillMaxWidth(),
//                )
//            }
//        }
//    }
//}
