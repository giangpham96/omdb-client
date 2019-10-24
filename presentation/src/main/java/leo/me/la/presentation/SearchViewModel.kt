package leo.me.la.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import leo.me.la.common.model.Movie
import leo.me.la.domain.SearchMoviesUseCase
import leo.me.la.exception.OmdbErrorException
import kotlin.math.ceil

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : BaseViewModel<SearchViewState>() {

    private var searchJob: Job? = null

    private val channel: ConflatedBroadcastChannel<String> = ConflatedBroadcastChannel()

    init {
        _viewStates.value = SearchViewState.Idling
        viewModelScope.launch {
            channel.asFlow()
                .onEach {
                    searchJob?.cancel()
                    _viewStates.value = SearchViewState.Searching
                }
                .transformLatest {
                    try {
                        val result = searchMoviesUseCase.execute(it)
                        emit(
                            SearchViewState.MoviesFetched(
                                it,
                                result.movies,
                                1,
                                ceil(result.totalResults.toFloat() / 10).toInt()
                            )
                        )
                    } catch (e: Throwable) {
                        when (e) {
                            is OmdbErrorException -> emit(
                                if (e.message == "Movie not found!")
                                    SearchViewState.MovieNotFound
                                else
                                    SearchViewState.SearchFailed(channel.value)
                            )
                            is CancellationException -> {}
                            else -> emit(SearchViewState.SearchFailed(channel.value))
                        }
                    }
                }
                .collect {
                    _viewStates.value = it
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
            channel.send(keyword)
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
                searchJob = viewModelScope.launch {
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

    override fun onCleared() {
        channel.close()
        searchJob?.cancel()
        super.onCleared()
    }
}

data class MovieInfoEvent(
    val movies: List<Movie>,
    val selectedMovie: String
)
