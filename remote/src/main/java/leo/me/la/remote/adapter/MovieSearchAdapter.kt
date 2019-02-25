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

internal class MovieSearchAdapter {
    @FromJson
    fun fromJson(
        reader: JsonReader,
        movieAdapter: JsonAdapter<RemoteMovieModel>
    ): RemoteMovieSearchModel {
        @Suppress("UNCHECKED_CAST") // This is a JSON object.
        val value = reader.readJsonValue() as Map<String, Any>
        when {
            value["Response"] == "True" -> {
                val searchResults = value["Search"] as? List<*> ?: throw UnexpectedException("Response misses `Search` field")
                val totalResults = value["totalResults"] as? String ?: throw UnexpectedException("Response misses `totalResults` field")
                return RemoteMovieSearchModel(
                    searchResults.map {
                        movieAdapter.fromJsonValue(it) ?: throw NullPointerException("Movie must not be null")
                    },
                    totalResults.toInt()
                )
            }
            value["Response"] == "False" -> throw OmdbErrorException(value["Error"] as? String ?: "Unknown Error")
            else -> throw UnexpectedException("Unexpected response")
        }
    }

    @ToJson
    fun toJson(
        writer: JsonWriter,
        content: RemoteMovieSearchModel?
    ) {
        throw UnsupportedOperationException("Cannot deserialize RemoteMovieSearchModel")
    }
}
