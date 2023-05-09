package leo.me.la.remote.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import leo.me.la.exception.OmdbErrorException
import leo.me.la.remote.model.MovieRemoteModel
import leo.me.la.remote.model.MovieSearchResultRemoteModel
import java.rmi.UnexpectedException

internal class MovieSearchAdapter(private val movieAdapter: MovieAdapter):
    JsonAdapter<MovieSearchResultRemoteModel>() {
    @FromJson
    override fun fromJson(
        reader: JsonReader
    ): MovieSearchResultRemoteModel {
        val result = mutableListOf<MovieRemoteModel>()
        var totalResults = 0
        reader.apply {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "Search" -> {
                        beginArray()
                        while (hasNext()) {
                            result.add(movieAdapter.fromJson(this))
                        }
                        endArray()
                    }
                    "totalResults" -> {
                        totalResults = nextInt()
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
            if (result.size == 0 || totalResults == 0) {
                throw UnexpectedException("Response misses field(s)")
            } else
                return MovieSearchResultRemoteModel(result, totalResults)
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: MovieSearchResultRemoteModel?) {
        throw UnsupportedOperationException("Cannot deserialize MovieSearchResultRemoteModel")
    }
}
