package leo.me.la.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import leo.me.la.common.model.Movie
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
        parentJob.cancel()
        _viewStates.value = SearchViewState.Idling
    }

    private val _navigationRequest = MutableLiveData<MovieInfoEvent>()
    val navigationRequest: LiveData<MovieInfoEvent>
        get() = _navigationRequest

    fun searchMovies(keyword: String) {
        parentJob.cancel()
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
                val fetchedMovies = when (this) {
                    is SearchViewState.MoviesFetched -> this.movies
                    is SearchViewState.LoadPageFailed -> this.movies
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
                _viewStates.value = SearchViewState.LoadingNextPage(fetchedMovies)
                if (parentJob.isCancelled)
                    parentJob = Job()
                launch {
                    try {
                        val nextPageMovieResult = searchMoviesUseCase.execute(keyword, nextPage)
                        _viewStates.value = SearchViewState.MoviesFetched(
                            keyword,
                            fetchedMovies + nextPageMovieResult.movies,
                            nextPage,
                            ceil(nextPageMovieResult.totalResults.toFloat() / 10).toInt()
                        )
                    } catch (ignored: CancellationException) {

                    } catch (e: Throwable) {
                        _viewStates.value = SearchViewState.LoadPageFailed(
                            keyword,
                            fetchedMovies,
                            nextPage,
                            totalPages,
                            e
                        )
                    }
                }
            }
        }
    }

    fun onItemClick(selectedMovie: String) {
        with(viewStates.value) {
            when (this) {
                is SearchViewState.MoviesFetched -> {
                    _navigationRequest.value = MovieInfoEvent(
                        this.movies,
                        selectedMovie
                    )
                }
                is SearchViewState.LoadPageFailed -> {
                    _navigationRequest.value = MovieInfoEvent(
                        this.movies,
                        selectedMovie
                    )
                }
                else -> Unit
            }
        }
    }
}

data class MovieInfoEvent(
    val movies: List<Movie>,
    val selectedMovie: String
)
