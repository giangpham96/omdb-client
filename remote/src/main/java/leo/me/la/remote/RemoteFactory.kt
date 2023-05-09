package leo.me.la.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Factory object to build Rest APIs and okhttp clients to use in remove module
 */
object RemoteFactory {

    /**
     * Builds given [T] retrofit restApi interface
     *
     * @param baseUrl [String] with a valid baseUrl including http scheme
     * @param restApi [T] with Retrofit interface
     * @param okHttpClient [OkHttpClient] to use when accessing this rest api
     */
    fun <T> buildRestApi(
        baseUrl: String,
        restApi: Class<T>,
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): T {
        return buildService(
            baseUrl,
            restApi,
            moshiConverterFactory,
            okHttpClient
        )
    }

    /**
     * Builds an [OkHttpClient] with the given interceptors attached to it
     *
     * @param restApiInterceptors [List] of [Interceptor] to attach to the expected client
     * @param restApiNetworkInterceptors [List] of network [Interceptor] to attach to the expected client
     */
    fun buildOkHttpClient(
        restApiInterceptors: List<Interceptor>?,
        restApiNetworkInterceptors: List<Interceptor>?
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                restApiInterceptors?.forEach { addInterceptor(it) }
                restApiNetworkInterceptors?.forEach { addNetworkInterceptor(it) }
            }
            .build()
    }

    private fun <T> buildService(
        baseUrl: String,
        restApi: Class<T>,
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient
    ): T {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
            .create(restApi)
    }
}
