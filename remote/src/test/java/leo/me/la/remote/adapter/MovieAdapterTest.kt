package leo.me.la.remote.adapter

import com.squareup.moshi.JsonReader
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import leo.me.la.exception.OmdbErrorException
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
            Movie(
                "Spider-Man: Into the Spider-Verse",
                "2018",
                "tt4633694",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_SX300.jpg",
                "PG",
                "14 Dec 2018",
                "117 min",
                listOf("Animation", "Action", "Adventure", "Family", "Sci-Fi"),
                listOf("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
                listOf(
                    "Phil Lord (screenplay by)",
                    "Rodney Rothman (screenplay by)",
                    "Phil Lord (story by)"
                ),
                listOf("Shameik Moore", "Jake Johnson", "Hailee Steinfeld", "Mahershala Ali"),
                "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                    "five counterparts from other dimensions to stop a threat for all realities.",
                listOf("English", "Spanish"),
                "USA",
                null,
                87,
                8.6,
                122126,
                null,
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

    private fun parseJsonToMovie(json: String) : Movie {
        return movieAdapter.fromJson(
            JsonReader.of(Buffer().writeUtf8(json))
        )
    }
}
