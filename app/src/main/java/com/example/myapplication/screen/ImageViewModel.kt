package com.example.myapplication.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ImageItem(val id: String, val url: String)

val apiKey = "kTlGplETXblHFLm8wC7LS5PXnkHWMzwn"

sealed class UiState {
    object Loading : UiState()
    data class Success(val images: List<ImageItem>) : UiState()
    data class Error(val message: String) : UiState()
}

class ImageGalleryViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val IMAGES_KEY = "loaded_images"
        private const val IMAGES_LIMIT = 50
    }

    private val _uiState = MutableStateFlow<UiState>(
        savedStateHandle.get<List<ImageItem>>(IMAGES_KEY)
            ?.let { UiState.Success(it) }
            ?: UiState.Loading
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        if (_uiState.value is UiState.Loading) {
            loadImages()
        }
    }

    fun loadImages() {
        viewModelScope.launch {
            try {
                val currentImages = (uiState.value as? UiState.Success)?.images ?: emptyList()
                if (currentImages.isEmpty()) {
                    _uiState.value = UiState.Loading
                }

                val images = fetchImagesFromApi()

                val mergedImages = (currentImages + images)
                    .distinctBy { it.id }
                    .take(IMAGES_LIMIT)

                val newState = UiState.Success(mergedImages)
                _uiState.value = newState

                savedStateHandle[IMAGES_KEY] = mergedImages
            } catch (e: IOException) {
                val currentImages = (uiState.value as? UiState.Success)?.images
                if (currentImages != null && currentImages.isNotEmpty()) {
                    _uiState.value = UiState.Success(currentImages)
                } else {
                    _uiState.value = UiState.Error("Нет подключения к интернету")
                }
            } catch (e: HttpException) {
                _uiState.value = UiState.Error("Ошибка сервера: ${e.code()}")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Неизвестная ошибка")
            }
        }
    }

    private suspend fun fetchImagesFromApi(): List<ImageItem> {
        return try {
            val response = NetworkModule.giphyApiService.getTrendingGifs(
                apiKey = apiKey,
                limit = IMAGES_LIMIT
            )
            response.gifs.map { gif ->
                ImageItem(
                    id = gif.id,
                    url = gif.images.downsizedMedium.url
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
