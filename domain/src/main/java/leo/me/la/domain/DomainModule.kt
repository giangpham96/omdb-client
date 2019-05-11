package leo.me.la.domain

import org.koin.dsl.module

val domainModule = module {
    factory<SearchMoviesUseCase> {
        SearchMoviesUseCaseImpl(get())
    }
    factory<LoadMovieInfoUseCase> {
        LoadMovieInfoUseCaseImpl(get())
    }
}
