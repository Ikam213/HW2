package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGalleryScreen(viewModel: ImageGalleryViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HW2") },
                actions = {
                    IconButton(onClick = { viewModel.loadImages() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingState()
                is UiState.Success -> SuccessState(state.images)
                is UiState.Error -> ErrorState(
                    errorMessage = state.message,
                    onRetry = { viewModel.loadImages() }
                )
            }
        }
    }
}


@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun SuccessState(images: List<ImageItem>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(images) { image ->
            ImageItem(imageUrl = image.url)
        }
    }
}

@Composable
fun ImageItem(imageUrl: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).decoderFactory(GifDecoder.Factory()).memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.ENABLED).build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Повторить загрузку")
            }
        }
    }
}