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
        var rated: String? = null
        var runtime: String? = null
        var genres: List<String>? = null
        var directors: List<String>? = null
        var writers: List<String>? = null
        var actors: List<String>? = null
        var plot: String? = null
        var languages: List<String>? = null
        var country: String? = null
        var awards: String? = null
        var metaScore: Int? = null
        var imdbRating: Double? = null
        var imdbVotes: Int? = null
        var boxOffice: String? = null
        var dvdRelease: String? = null
        var released: String? = null
        var production: String? = null
        var website: String? = null
        reader.apply {
            beginObject()
            while (hasNext()) {
                when(nextName()) {
                    "Title" -> title = nextString()
                    "Year" -> year = nextString()
                    "Poster" -> poster = parseString(nextString())
                    "imdbID" -> imdbId = nextString()
                    "Type" -> type = when (nextString()) {
                        "movie" -> MovieType.Movie
                        "series" -> MovieType.Series
                        else -> MovieType.Other
                    }
                    "Rated" -> rated = parseString(nextString())
                    "Released" -> released = parseString(nextString())
                    "Runtime" -> runtime = parseString(nextString())
                    "Genre" -> genres = parseList(nextString())
                    "Director" -> directors = parseList(nextString())
                    "Writer" -> writers = parseList(nextString())
                    "Actors" -> actors = parseList(nextString())
                    "Plot" -> plot = parseString(nextString())
                    "Language" -> languages = parseList(nextString())
                    "Country" -> country = parseString(nextString())
                    "Awards" -> awards = parseString(nextString())
                    "Metascore" -> metaScore = parseString(nextString())?.toInt()
                    "imdbRating" -> imdbRating = parseString(nextString())?.toDouble()
                    "imdbVotes" -> imdbVotes = parseString(nextString().replace(",",""))?.toInt()
                    "DVD" -> dvdRelease = parseString(nextString())
                    "BoxOffice" -> boxOffice = parseString(nextString())
                    "Production" -> production = parseString(nextString())
                    "Website" -> website = parseString(nextString())
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
            return Movie(
                title, year, imdbId, type!!, poster,
                rated, released, runtime, genres, directors,
                writers, actors, plot, languages, country,
                awards, metaScore, imdbRating, imdbVotes, boxOffice,
                dvdRelease, production, website
            )
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

    private fun parseList(string: String) : List<String>? {
        return if (string == "N/A")
            null
        else
            string.split(", ")
    }

    private fun parseString(string: String) : String? {
        return if (string == "N/A")
            null
        else
            string
    }
}
