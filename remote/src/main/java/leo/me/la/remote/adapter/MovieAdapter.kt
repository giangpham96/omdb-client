package leo.me.la.remote.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import leo.me.la.exception.OmdbErrorException
import java.rmi.UnexpectedException
internal class MovieAdapter {
    @FromJson
    fun fromJson(
        reader: JsonReader
    ): Movie {
        var title = ""
        var year = ""
        var poster: String? = null
        var imdbId = ""
        var type: MovieType? = null
        reader.apply {
            beginObject()
            while (hasNext()) {
                when(nextName()) {
                    "Title" -> title = nextString()
                    "Year" -> year = nextString()
                    "Poster" -> poster = nextString()?.let { if (it == "N/A") null else it }
                    "imdbID" -> imdbId = nextString()
                    "Type" -> type = when (nextString()) {
                        "movie" -> MovieType.Movie
                        "series" -> MovieType.Series
                        else -> MovieType.Other
                    }
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
        if (!title.isEmpty() && !year.isEmpty() && !imdbId.isEmpty() && type != null) {
            return Movie(title, year, imdbId, type!!, poster)
        } else
            throw UnexpectedException("Response misses field(s)")
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
