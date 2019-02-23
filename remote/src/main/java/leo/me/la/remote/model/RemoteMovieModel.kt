package leo.me.la.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMovieModel(
    @Json(name = "Title") val title: String,
    @Json(name = "Year") val year: String,
    @Json(name = "Type") val type: String,
    @Json(name = "Poster") val poster: String
)
