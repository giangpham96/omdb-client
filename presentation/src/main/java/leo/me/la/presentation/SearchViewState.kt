package leo.me.la.presentation

import leo.me.la.common.model.Movie

data class SearchViewState(
    val data: DataState<SearchUi>,
    val keyword: String? = null,
) {
    data class SearchUi(
        val keyword: String,
        val movies: List<Movie>,
        val page: Int,
        val totalPages: Int,
        val nextPageLoading: Boolean,
        val showReloadNextPage: Boolean,
    )
}
