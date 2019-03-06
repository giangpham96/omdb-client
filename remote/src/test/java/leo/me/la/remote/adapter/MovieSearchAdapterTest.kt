package leo.me.la.remote.adapter

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import leo.me.la.exception.OmdbErrorException
import leo.me.la.remote.model.RemoteMovieModel
import leo.me.la.remote.model.RemoteMovieSearchModel
import leo.me.la.remote.readFileContent
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.rmi.UnexpectedException

class MovieSearchAdapterTest {

    private val moshi = Moshi.Builder().build()
    private val movieSearchAdapter = MovieSearchAdapter()
    private val movieAdapter = moshi.adapter(RemoteMovieModel::class.java)

    @Test
    fun `should parse successfully if Response field is "True" and json fields are not missing`() {
        val json = "json/search-result.json".readFileContent()
        val parsingResult = parseJsonToRemoteMovieSearchModel(json)
        assertThat(parsingResult.result.size == 3)
        assertThat(parsingResult.totalResults == 3)
    }

    @Test(expected = OmdbErrorException::class)
    fun `should throw OmdbErrorException if Response field is "False"`() {
        val json = "json/error-result.json".readFileContent()
        parseJsonToRemoteMovieSearchModel(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if Response field is "True" but "Search" field is missing`() {
        val json = "json/search-result-without-search-field.json".readFileContent()
        parseJsonToRemoteMovieSearchModel(json)
    }

    @Test(expected = UnexpectedException::class)
    fun `should throw UnexpectedException if Response field is "True" but "totalResults" field is missing`() {
        val json = "json/search-result-without-total-results-field.json".readFileContent()
        parseJsonToRemoteMovieSearchModel(json)
    }

    private fun parseJsonToRemoteMovieSearchModel(json: String) : RemoteMovieSearchModel {
        return movieSearchAdapter.fromJson(
            JsonReader.of(Buffer().writeUtf8(json)),
            movieAdapter
        )
    }
}
