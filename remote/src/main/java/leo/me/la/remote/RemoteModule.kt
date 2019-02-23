package leo.me.la.remote

import leo.me.la.common.TAG_INTERCEPTOR_API_KEY
import leo.me.la.common.TAG_OMDB_API_KEY
import leo.me.la.common.TAG_OMDB_RETROFIT
import org.koin.dsl.module.module

val remoteModule = module {

    single {
        RemoteFactory.buildOkHttpClient(
            listOf(get(name = TAG_INTERCEPTOR_API_KEY)),
            emptyList()
        )
    }

    factory(name = TAG_OMDB_RETROFIT) {
        RemoteFactory.buildRestApi(
            "http://www.omdbapi.com/",
            OmdbRestApi::class.java,
            get()
        )
    }

    factory(name = TAG_INTERCEPTOR_API_KEY) {
        ApiKeyInterceptor(get(name = TAG_OMDB_API_KEY))
    }
}
