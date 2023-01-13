package leo.me.la.presentation

import leo.me.la.common.TAG_MOVIE_INFO_VIEWMODEL
import leo.me.la.common.TAG_SEARCH_VIEWMODEL
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule = module {
    baseViewModel(TAG_SEARCH_VIEWMODEL) {
        SearchViewModel(get())
    }
    baseViewModel(TAG_MOVIE_INFO_VIEWMODEL) { (imdbId : String, poster: String?) ->
        MovieInfoViewModel(get(), imdbId = imdbId, poster = poster)
    }
}

inline fun <reified T : BaseViewState> Module.baseViewModel(
    name: String = "",
    noinline definition: Definition<BaseViewModel<T>>
) {
    viewModel(named(name), definition)
}
