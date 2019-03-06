package leo.me.la.remote.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import leo.me.la.exception.OmdbErrorException
import leo.me.la.remote.model.RemoteMovieModel
import leo.me.la.remote.model.RemoteMovieSearchModel
import java.rmi.UnexpectedException

internal class MovieAdapter {
    @FromJson
    fun fromJson(
        reader: JsonReader
    ): RemoteMovieModel {
        var title = ""
        var year = ""
        var poster: String? = null
        var imdbId = ""
        var type = ""
        reader.apply {
            beginObject()
            while (hasNext()) {
                when(nextName()) {
                    "Title" -> title = nextString()
                    "Year" -> year = nextString()
                    "Poster" -> poster = nextString()?.let { if (it == "N/A") null else it }
                    "imdbID" -> imdbId = nextString()
                    "Type" -> type = nextString()
                    "Error" -> {
                        val errorMessage = nextString()
                        endObject()
                        throw OmdbErrorException(errorMessage)
                    }
                    else -> skipValue()
                }
            }
            endObject()
        }
        if (title.isEmpty() || year.isEmpty() || imdbId.isEmpty() || type.isEmpty()) {
            throw UnexpectedException("Response misses field(s)")
        }
        return RemoteMovieModel(title, year, imdbId, type, poster)
    }

    @Suppress("Unused", "UNUSED_PARAMETER")
    @ToJson
    fun toJson(
        writer: JsonWriter,
        content: Movie?
    ) {
        throw UnsupportedOperationException("Cannot deserialize Movie")
    }
}
