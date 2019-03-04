package leo.me.la.presentation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import leo.me.la.domain.SearchMoviesUseCase
import leo.me.la.exception.OmdbErrorException
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    context: CoroutineContext = Dispatchers.Main
) : BaseViewModel<SearchViewState>(context) {

    init {
        _viewStates.value = SearchViewState.Idling
    }

    fun resetSearch() {
        if (isParentJobInitialized()) {
            parentJob.cancel()
        }
        _viewStates.value = SearchViewState.Idling
    }

    fun searchMovies(keyword: String) {
        if (isParentJobInitialized()) {
            parentJob.cancel()
        }
        parentJob = Job()
        launch {
            _viewStates.value = SearchViewState.Searching
            try {
                val movieResult = searchMoviesUseCase.execute(keyword)
                _viewStates.value = SearchViewState.MoviesFetched(
                    keyword,
                    movieResult.movies,
                    1,
                    ceil(movieResult.totalResults.toFloat() / 10).toInt()
                )
            } catch (e: OmdbErrorException) {
                if (e.message == "Movie not found!")
                    _viewStates.value = SearchViewState.MovieNotFound
                else
                    _viewStates.value = SearchViewState.SearchFailed(keyword)
            } catch (ignored: CancellationException) {

            } catch (e: Throwable) {
                _viewStates.value = SearchViewState.SearchFailed(keyword)
            }
        }
    }

    fun loadNextPage() {
        with(viewStates.value) {
            if (this is SearchViewState.MoviesFetched
                || this is SearchViewState.LoadPageFailed
            ) {
                val totalPages = when (this) {
                    is SearchViewState.MoviesFetched -> this.totalPages
                    is SearchViewState.LoadPageFailed -> this.totalPages
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                val nextPage = when (this) {
                    is SearchViewState.MoviesFetched -> this.page + 1
                    is SearchViewState.LoadPageFailed -> this.pageFailedToLoad
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                if (totalPages < nextPage || totalPages >= 100) {
                    return@with
                }
                val keyword = when (this) {
                    is SearchViewState.MoviesFetched -> this.keyword
                    is SearchViewState.LoadPageFailed -> this.keyword
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                _viewStates.value = SearchViewState.LoadingNextPage

                launch {
                    try {
                        val nextPageMovieResult = searchMoviesUseCase.execute(keyword, nextPage)
                        _viewStates.value = SearchViewState.MoviesFetched(
                            keyword,
                            nextPageMovieResult.movies,
                            nextPage,
                            ceil(nextPageMovieResult.totalResults.toFloat() / 10).toInt()
                        )
                    } catch (ignored: CancellationException) {

                    } catch (e: Throwable) {
                        _viewStates.value = SearchViewState.LoadPageFailed(
                            keyword,
                            nextPage,
                            totalPages,
                            e
                        )
                    }
                }
            }
        }
    }
}
