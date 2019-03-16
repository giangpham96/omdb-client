package leo.me.la.remote.adapter

import com.squareup.moshi.JsonReader
import leo.me.la.exception.OmdbErrorException
import leo.me.la.remote.model.MovieRemoteModel
import leo.me.la.remote.readFileContent
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.rmi.UnexpectedException

class MovieAdapterTest {
    private val movieAdapter = MovieAdapter()

    @Test
    fun `should parse successfully if Response field is "True" and json fields are not missing`() {
        val json = "json/imdb-id-search-result.json".readFileContent()
        val parsingResult = parseJsonToMovie(json)
        assertThat(parsingResult).isEqualTo(
            MovieRemoteModel(
                "Spider-Man: Into the Spider-Verse",
                "2018",
                "tt4633694",
                "movie",
                "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_SX300.jpg",
                "PG",
                "14 Dec 2018",
                "117 min",
                "Animation, Action, Adventure, Family, Sci-Fi",
                "Bob Persichetti, Peter Ramsey, Rodney Rothman",
                "Phil Lord (screenplay by), Rodney Rothman (screenplay by), Phil Lord (story by)",
                "Shameik Moore, Jake Johnson, Hailee Steinfeld, Mahershala Ali",
                "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                    "five counterparts from other dimensions to stop a threat for all realities.",
                "English, Spanish",
                "USA",
                "N/A",
                "87",
                "8.6",
                "122,126",
                "N/A",
                "26 Feb 2019",
                "Sony Pictures",
                "http://www.intothespiderverse.movie/"
            )
        )
    }

    @Test(expected = OmdbErrorException::class)
    fun `should throw OmdbErrorException if Error field exists`() {
        val json = "json/error-result.json".readFileContent()
        parseJsonToMovie(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if imdbId is missing`() {
        val json = "json/imdb-id-search-result-without-imdb-id.json".readFileContent()
        parseJsonToMovie(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if title is missing`() {
        val json = "json/imdb-id-search-result-without-title.json".readFileContent()
        parseJsonToMovie(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if type is missing`() {
        val json = "json/imdb-id-search-result-without-type.json".readFileContent()
        parseJsonToMovie(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if year is missing`() {
        val json = "json/imdb-id-search-result-without-year.json".readFileContent()
        parseJsonToMovie(json)
    }

    private fun parseJsonToMovie(json: String) : MovieRemoteModel {
        return movieAdapter.fromJson(
            JsonReader.of(Buffer().writeUtf8(json))
        )
    }
}
