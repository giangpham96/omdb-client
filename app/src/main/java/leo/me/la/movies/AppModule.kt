package leo.me.la.movies

import leo.me.la.common.TAG_BOOLEAN_DEBUG
import leo.me.la.common.TAG_OMDB_API_KEY
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named(TAG_BOOLEAN_DEBUG)) {
        BuildConfig.DEBUG
    }
    single(named(TAG_OMDB_API_KEY)) {
        BuildConfig.OMDB_API_KEY
    }
}
