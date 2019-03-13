package leo.me.la.presentation

import leo.me.la.common.model.Movie

sealed class SearchViewState : BaseViewState {
    object Idling : SearchViewState()
    object Searching : SearchViewState()
    data class LoadingNextPage(
        val movies: List<Movie>
    ) : SearchViewState()
    data class MoviesFetched(
        val keyword: String,
        val movies: List<Movie>,
        val page: Int,
        val totalPages: Int
    ) : SearchViewState()
    data class LoadPageFailed(
        val keyword: String,
        val movies: List<Movie>,
        val pageFailedToLoad: Int,
        val totalPages: Int,
        val reason: Throwable? = null
    ) : SearchViewState()
    data class SearchFailed(val keyword: String) : SearchViewState()
    object MovieNotFound : SearchViewState()
}
