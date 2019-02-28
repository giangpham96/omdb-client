package leo.me.la.presentation

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val presentationModule = module {
    viewModel {
        SearchViewModel(get())
    }
}
