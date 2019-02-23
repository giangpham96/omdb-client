package leo.me.la.remote

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor(private val apiKey: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            with(chain.request()) {
                newBuilder().url(
                    url().newBuilder()
                        .addQueryParameter("apikey", apiKey)
                        .build()
                ).build()
            }
        )
    }
}
