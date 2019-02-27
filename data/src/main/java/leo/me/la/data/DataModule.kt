package leo.me.la.data

import leo.me.la.domain.repository.SearchRepository
import org.koin.dsl.module.module

val dataModule = module {
    factory<SearchRepository> {
        SearchRepositoryImpl(get())
    }
}
