package leo.me.la.remote

import okhttp3.Interceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After

/**
 * Base class to make Api calls
 */
abstract class BaseApiTest {

    protected val mockServer: MockWebServer = MockWebServer()

    init {
        mockServer.start()
    }

    protected inline fun <reified T : Any> getMockedRestApi(
        restApiInterceptors: List<Interceptor>? = null,
        restApiNetworkInterceptors: List<Interceptor>? = null
    ): T {
        return RemoteFactory.buildRestApi(
            mockServer.url("/").toString(),
            T::class.java,
            RemoteFactory.buildOkHttpClient(restApiInterceptors, restApiNetworkInterceptors)
        )
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    /**
     * Adds to the response queue given [List] of [MockResponse]s
     */
    protected fun MockWebServer.enqueue(vararg responses: MockResponse) {
        responses.forEach { this.enqueue(it) }
    }
}
