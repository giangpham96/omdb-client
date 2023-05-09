package leo.me.la.remote.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import leo.me.la.exception.OmdbErrorException
import leo.me.la.remote.model.MovieRemoteModel
import java.rmi.UnexpectedException

internal class MovieAdapter : JsonAdapter<MovieRemoteModel>() {
    @FromJson
    override fun fromJson(
        reader: JsonReader
    ): MovieRemoteModel {
        var title = ""
        var year = ""
        var poster = ""
        var imdbId = ""
        var type = ""
        var rated: String? = null
        var runtime: String? = null
        var genres: String? = null
        var directors: String? = null
        var writers: String? = null
        var actors: String? = null
        var plot: String? = null
        var languages: String? = null
        var country: String? = null
        var awards: String? = null
        var metaScore: String? = null
        var imdbRating: String? = null
        var imdbVotes: String? = null
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
                    "Poster" -> poster = nextString()
                    "imdbID" -> imdbId = nextString().trim()
                    "Type" -> type = nextString()
                    "Rated" -> rated = nextString()
                    "Released" -> released = nextString()
                    "Runtime" -> runtime = nextString()
                    "Genre" -> genres = nextString()
                    "Director" -> directors = nextString()
                    "Writer" -> writers = nextString()
                    "Actors" -> actors = nextString()
                    "Plot" -> plot = nextString()
                    "Language" -> languages = nextString()
                    "Country" -> country = nextString()
                    "Awards" -> awards = nextString()
                    "Metascore" -> metaScore = nextString()
                    "imdbRating" -> imdbRating = nextString()
                    "imdbVotes" -> imdbVotes = nextString()
                    "DVD" -> dvdRelease = nextString()
                    "BoxOffice" -> boxOffice = nextString()
                    "Production" -> production = nextString()
                    "Website" -> website = nextString()
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
        if (title.isNotEmpty() && year.isNotEmpty() && imdbId.isNotEmpty() && type.isNotEmpty()) {
            return MovieRemoteModel(
                title, year, imdbId, type, poster,
                rated, released, runtime, genres, directors,
                writers, actors, plot, languages, country,
                awards, metaScore, imdbRating, imdbVotes, boxOffice,
                dvdRelease, production, website
            )
        } else
            throw UnexpectedException("Response misses field(s)")
    }

    @ToJson
    override fun toJson(
        writer: JsonWriter,
        content: MovieRemoteModel?
    ) {
        throw UnsupportedOperationException("Cannot deserialize Movie")
    }
}
