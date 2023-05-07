package leo.me.la.presentation

import leo.me.la.presentation.SearchViewState.SearchUi
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


data class SearchViewState(
    val searchState: DataState<SearchUi>,
    val keyword: String = "",
) {
    data class SearchUi(
        val keyword: String,
        val movies: List<Movie>,
        val footer: SearchFooterItem?,
        val page: Int,
        val totalPages: Int,
    ) {
        val listItems = movies + listOfNotNull(footer)
        sealed interface SearchItem
        sealed interface SearchFooterItem: SearchItem
        data class Movie(
            val title: String,
            val poster: String?,
            val imdbId: String,
        ): SearchItem

        object LoadingNextPage: SearchFooterItem
        object ReloadNextPage: SearchFooterItem
    }
}


@OptIn(ExperimentalContracts::class)
fun canLoadNextPage(state: DataState<SearchUi>): Boolean {
    contract {
        returns(true) implies (state is DataState.Success)
    }
    if (state !is DataState.Success) return false
    val footer = state.data.footer
    return footer != SearchUi.LoadingNextPage
}
