package leo.me.la.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import leo.me.la.common.model.Movie
import leo.me.la.domain.SearchMoviesUseCase
import leo.me.la.exception.OmdbErrorException
import leo.me.la.presentation.SearchViewState.LoadPageFailed
import leo.me.la.presentation.SearchViewState.MovieNotFound
import leo.me.la.presentation.SearchViewState.MoviesFetched
import leo.me.la.presentation.SearchViewState.SearchFailed
import kotlin.math.ceil

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : BaseViewModel<SearchViewState>() {

    private var searchJob: Job? = null

    private val channel = MutableSharedFlow<String>()

    init {
        _viewStates.value = SearchViewState.Idling
        viewModelScope.launch {
            channel.collectLatest {
                searchJob?.cancel()
                _viewStates.value = SearchViewState.Searching
                try {
                    val result = searchMoviesUseCase.execute(it)
                    _viewStates.value = MoviesFetched(
                        keyword = it,
                        movies = result.movies,
                        page = 1,
                        totalPages = ceil(result.totalResults.toFloat() / 10).toInt()
                    )
                } catch (ignored: CancellationException) {
                    throw ignored
                } catch (e: Throwable) {
                    _viewStates.value = when (e) {
                        is OmdbErrorException -> {
                            if (e.message == "Movie not found!")
                                MovieNotFound
                            else
                                SearchFailed(it)
                        }

                        else -> SearchFailed(it)
                    }
                }
            }
        }
    }

    fun resetSearch() {
        searchJob?.cancel()
        _viewStates.value = SearchViewState.Idling
    }

    private val _navigationRequest = MutableLiveData<MovieInfoEvent>()
    val navigationRequest: LiveData<MovieInfoEvent>
        get() = _navigationRequest

    fun searchMovies(keyword: String) {
        viewModelScope.launch {
            channel.emit(keyword)
        }
    }

    fun loadNextPage() {
        with(viewStates.value) {
            if (this is MoviesFetched || this is LoadPageFailed) {
                val totalPages = when (this) {
                    is MoviesFetched -> this.totalPages
                    is LoadPageFailed -> this.totalPages
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                val nextPage = when (this) {
                    is MoviesFetched -> this.page + 1
                    is LoadPageFailed -> this.pageFailedToLoad
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                val fetchedMovies = when (this) {
                    is MoviesFetched -> this.movies
                    is LoadPageFailed -> this.movies
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                if (totalPages < nextPage || totalPages >= 100) {
                    return@with
                }
                val keyword = when (this) {
                    is MoviesFetched -> this.keyword
                    is LoadPageFailed -> this.keyword
                    else -> throw IllegalStateException("The state ${this.javaClass.simpleName} is unexpected")
                }
                _viewStates.value = SearchViewState.LoadingNextPage(fetchedMovies)
                searchJob = viewModelScope.launch {
                    try {
                        val nextPageMovieResult = searchMoviesUseCase.execute(keyword, nextPage)
                        _viewStates.value = MoviesFetched(
                            keyword,
                            fetchedMovies + nextPageMovieResult.movies,
                            nextPage,
                            ceil(nextPageMovieResult.totalResults.toFloat() / 10).toInt()
                        )
                    } catch (ignored: CancellationException) {
                        throw ignored
                    } catch (e: Throwable) {
                        _viewStates.value = LoadPageFailed(
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
                is MoviesFetched -> {
                    _navigationRequest.value = MovieInfoEvent(
                        this.movies,
                        selectedMovie
                    )
                }

                is LoadPageFailed -> {
                    _navigationRequest.value = MovieInfoEvent(
                        this.movies,
                        selectedMovie
                    )
                }

                else -> Unit
            }
        }
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }
}

data class MovieInfoEvent(
    val movies: List<Movie>,
    val selectedMovie: String,
)
