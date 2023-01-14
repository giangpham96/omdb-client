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
import leo.me.la.presentation.DataState.Failure
import leo.me.la.presentation.DataState.Idle
import leo.me.la.presentation.DataState.Loading
import leo.me.la.presentation.DataState.Success
import leo.me.la.presentation.SearchViewState.SearchUi
import kotlin.math.ceil

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : BaseViewModel<SearchViewState>() {

    private var searchJob: Job? = null

    private val channel = MutableSharedFlow<String>()

    init {
        _viewStates.value = SearchViewState(Idle)
        viewModelScope.launch {
            channel.collectLatest {
                searchJob?.cancel()
                _viewStates.value = SearchViewState(Loading, keyword = it)
                try {
                    val result = searchMoviesUseCase.execute(it)
                    _viewStates.value = SearchViewState(
                        Success(
                            SearchUi(
                                keyword = it,
                                movies = result.movies,
                                page = 1,
                                totalPages = ceil(result.totalResults.toFloat() / 10).toInt(),
                                nextPageLoading = false,
                                showReloadNextPage = false,
                            )
                        ),
                        keyword = it,
                    )
                } catch (ignored: CancellationException) {
                    throw ignored
                } catch (e: Throwable) {
                    _viewStates.value = when (e) {
                        is OmdbErrorException -> {
                            val err = if (e.message == "Movie not found!")
                                RuntimeException(e.message)
                            else
                                null
                            SearchViewState(Failure(err), keyword = it)
                        }

                        else -> SearchViewState(Failure(), keyword = it)
                    }
                }
            }
        }
    }

    fun resetSearch() {
        searchJob?.cancel()
        _viewStates.value = SearchViewState(Idle)
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
            val data = this?.data
            if (data is Success && (data.data.showReloadNextPage || !data.data.nextPageLoading)) {
                val totalPages = data.data.totalPages
                val nextPage = data.data.page + 1
                val fetchedMovies = data.data.movies
                if (totalPages < nextPage || totalPages >= 100) {
                    return@with
                }
                val keyword = data.data.keyword
                _viewStates.value = SearchViewState(
                    data = Success(data.data.copy(showReloadNextPage = false, nextPageLoading = true)),
                    keyword = viewStates.value?.keyword,
                )
                searchJob = viewModelScope.launch {
                    try {
                        val nextPageMovieResult = searchMoviesUseCase.execute(keyword, nextPage)
                        _viewStates.value = SearchViewState(
                            data = Success(
                                data.data.copy(
                                    movies = fetchedMovies + nextPageMovieResult.movies,
                                    page = nextPage,
                                    showReloadNextPage = false,
                                    nextPageLoading = false,
                                )
                            ),
                            keyword = viewStates.value?.keyword,
                        )
                    } catch (ignored: CancellationException) {
                        throw ignored
                    } catch (e: Throwable) {
                        _viewStates.value = SearchViewState(
                            data = Success(
                                data.data.copy(
                                    showReloadNextPage = true,
                                    nextPageLoading = false,
                                )
                            ),
                            keyword = viewStates.value?.keyword,
                        )
                    }
                }
            }
        }
    }

    fun onItemClick(selectedMovie: String) {
        with(viewStates.value?.data) {
            when (this) {
                is Success -> {
                    _navigationRequest.value = MovieInfoEvent(
                        data.movies,
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
