package com.example.myapplication.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class GiphyResponse(
    @SerializedName("data") val gifs: List<GifItem>
)

data class GifItem(
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: GifImages
)

data class GifImages(
    @SerializedName("downsized_medium") val downsizedMedium: ImageVariant
)

data class ImageVariant(
    @SerializedName("url") val url: String
)

interface GiphyApiService {
    @GET("gifs/trending")
    suspend fun getTrendingGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 50,
        @Query("rating") rating: String = "g"
    ): GiphyResponse
}
