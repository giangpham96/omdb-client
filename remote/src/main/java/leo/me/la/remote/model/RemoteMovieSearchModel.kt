package leo.me.la.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMovieSearchModel(
    @Json(name = "Search") val result: List<RemoteMovieModel>,
    val totalResults: Int
)
