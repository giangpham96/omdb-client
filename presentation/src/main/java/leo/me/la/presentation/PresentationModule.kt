package leo.me.la.presentation

import leo.me.la.common.TAG_SEARCH_VIEWMODEL
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val presentationModule = module {
    viewModel<BaseViewModel<SearchViewState>>(TAG_SEARCH_VIEWMODEL) {
        SearchViewModel(get())
    }
}
