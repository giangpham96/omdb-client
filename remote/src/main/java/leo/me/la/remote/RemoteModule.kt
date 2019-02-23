package leo.me.la.remote

import leo.me.la.common.TAG_BOOLEAN_DEBUG
import leo.me.la.common.TAG_INTERCEPTOR_API_KEY
import leo.me.la.common.TAG_INTERCEPTOR_LOGGING
import leo.me.la.common.TAG_OMDB_API_KEY
import leo.me.la.common.TAG_OMDB_RETROFIT
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module

val remoteModule = module {

    single {
        RemoteFactory.buildOkHttpClient(
            listOf(get(name = TAG_INTERCEPTOR_API_KEY)),
            listOf(get(name = TAG_INTERCEPTOR_LOGGING))
        )
    }

    factory(name = TAG_OMDB_RETROFIT) {
        RemoteFactory.buildRestApi(
            "http://www.omdbapi.com/",
            OmdbRestApi::class.java,
            get()
        )
    }

    factory(name = TAG_INTERCEPTOR_LOGGING) {
        val isDebug: Boolean = get(name = TAG_BOOLEAN_DEBUG)
        HttpLoggingInterceptor().apply {
            level = if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    factory(name = TAG_INTERCEPTOR_API_KEY) {
        ApiKeyInterceptor(get(name = TAG_OMDB_API_KEY))
    }
}
