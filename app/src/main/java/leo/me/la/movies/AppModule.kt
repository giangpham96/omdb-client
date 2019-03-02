package leo.me.la.movies

import leo.me.la.common.TAG_BOOLEAN_DEBUG
import leo.me.la.common.TAG_OMDB_API_KEY
import org.koin.dsl.module.module

val appModule = module {
    single(TAG_BOOLEAN_DEBUG) {
        BuildConfig.DEBUG
    }
    single(TAG_OMDB_API_KEY) {
        BuildConfig.OMDB_API_KEY
    }
}
