package com.saitejajanjirala.cvs_task.ui.home

import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.ui.MainViewModel
import com.saitejajanjirala.cvs_task.domain.util.Result
import com.saitejajanjirala.cvs_task.ui.util.AsyncImageWithPlaceholder
import com.saitejajanjirala.cvs_task.ui.util.Screen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MainViewModel = hiltViewModel(),
    onImageClicked:(item: Item)->Unit) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val result by viewModel.searchResults.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Search TextField
        BasicTextField(
            value = searchQuery,
            onValueChange = { query -> viewModel.updateSearchQuery(query) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(text = "Search...")
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


        when (result) {
            is Result.Loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Result.Success -> {   val items = result.d ?: emptyList()
                // Grid View of Images
                LazyColumn (

                    modifier = Modifier.fillMaxSize(),

                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        AsyncImageWithPlaceholder(item,modifier = Modifier.clickable {
                           onImageClicked(item)
                        }.fillMaxWidth()
                            .height(150.dp).sharedElement(
                            state = rememberSharedContentState(
                                key = "img-${item.published}"
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 1000)
                            }
                        ))
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            is Result.Error -> {
                val errorMessage = result.m
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage ?: "An unknown error occurred.")
                }
            }
            is Result.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Search for images...")
                }
            }
        }
    }
}
