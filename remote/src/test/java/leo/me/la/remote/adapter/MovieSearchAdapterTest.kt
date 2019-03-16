package leo.me.la.remote.adapter

import com.squareup.moshi.JsonReader
import leo.me.la.exception.OmdbErrorException
import leo.me.la.remote.model.MovieSearchResultRemoteModel
import leo.me.la.remote.readFileContent
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.rmi.UnexpectedException

class MovieSearchAdapterTest {

    private val movieSearchAdapter = MovieSearchAdapter(MovieAdapter())

    @Test
    fun `should parse successfully if Response field is "True" and json fields are not missing`() {
        val json = "json/search-result.json".readFileContent()
        val parsingResult = parseJsonToMovieSearchResult(json)
        assertThat(parsingResult.movies.size == 3)
        assertThat(parsingResult.totalResults == 3)
    }

    @Test(expected = OmdbErrorException::class)
    fun `should throw OmdbErrorException if Error field exists`() {
        val json = "json/error-result.json".readFileContent()
        parseJsonToMovieSearchResult(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if "Search" field is missing`() {
        val json = "json/search-result-without-search-field.json".readFileContent()
        parseJsonToMovieSearchResult(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if "totalResults" field is missing`() {
        val json = "json/search-result-without-total-results-field.json".readFileContent()
        parseJsonToMovieSearchResult(json)
    }

    private fun parseJsonToMovieSearchResult(json: String) : MovieSearchResultRemoteModel {
        return movieSearchAdapter.fromJson(
            JsonReader.of(Buffer().writeUtf8(json))
        )
    }
}
