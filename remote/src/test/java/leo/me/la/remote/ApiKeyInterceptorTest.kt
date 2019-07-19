package leo.me.la.remote

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

internal class ApiKeyInterceptorTest : BaseApiTest() {
    private lateinit var restApi: OmdbRestApi
    private val interceptor = ApiKeyInterceptor("secret")

    @Before
    fun setup() {
        restApi = getMockedRestApi(restApiInterceptors = listOf(interceptor))
    }

    @Test
    fun `should add apikey parameter if it does not exist`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setBody("json/search-result.json".readFileContent())
                    .setResponseCode(200)
            )
            // execute
            restApi.searchByKeyword("Batman", 2).await()
            // verify
            assertThat(
                mockServer.takeRequest()
                    .requestUrl
                    .queryParameter("apikey")
            ).isEqualTo("secret")
        }
    }
}
