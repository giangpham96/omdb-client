package leo.me.la.presentation

import leo.me.la.common.TAG_SEARCH_VIEWMODEL
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.Definition
import org.koin.dsl.module.module

val presentationModule = module {
    baseViewModel(TAG_SEARCH_VIEWMODEL) {
        SearchViewModel(get())
    }
}

inline fun <reified T : BaseViewState> ModuleDefinition.baseViewModel(
    name: String = "",
    override: Boolean = false,
    noinline definition: Definition<BaseViewModel<T>>
) {
    viewModel(name, override, definition)
}
