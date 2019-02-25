package leo.me.la.domain

import org.koin.dsl.module.module

val domainModule = module {
    factory {
        SearchMoviesUseCase(get())
    }
}
