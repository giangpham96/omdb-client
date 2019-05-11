package leo.me.la.data

import leo.me.la.domain.repository.MovieRepository
import org.koin.dsl.module

val dataModule = module {
    factory<MovieRepository> {
        MovieRepositoryImpl(get(), get())
    }
}
