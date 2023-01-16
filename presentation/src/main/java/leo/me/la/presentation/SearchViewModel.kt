package leo.me.la.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val keywordFlow = MutableSharedFlow<String>()

    override val _viewState = MutableStateFlow(SearchViewState(Idle))

    init {
        viewModelScope.launch {
            keywordFlow.collectLatest {
                searchJob?.cancel()
                _viewState.value = SearchViewState(Loading, keyword = it)
                try {
                    val result = searchMoviesUseCase.execute(it)
                    _viewState.value = SearchViewState(
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
                    _viewState.value = when (e) {
                        is OmdbErrorException -> {
                            val err = if (e.message == "Movie not found!")
                                MovieNotFoundException
                            else
                                null
                            SearchViewState(Failure(err ?: RuntimeException()), keyword = it)
                        }

                        else -> SearchViewState(Failure(), keyword = it)
                    }
                }
            }
        }
    }

    fun resetSearch() {
        searchJob?.cancel()
        _viewState.value = SearchViewState(Idle)
    }

    private val _navigationRequest = MutableSharedFlow<MovieInfoEvent>()
    val navigationRequest: SharedFlow<MovieInfoEvent>
        get() = _navigationRequest

    fun searchMovies(keyword: String) {
        viewModelScope.launch {
            keywordFlow.emit(keyword)
        }
    }

    fun loadNextPage() {
        with(viewState.value) {
            if (searchState is Success && (searchState.data.showReloadNextPage || !searchState.data.nextPageLoading)) {
                val totalPages = searchState.data.totalPages
                val nextPage = searchState.data.page + 1
                val fetchedMovies = searchState.data.movies
                if (totalPages < nextPage || totalPages >= 100) {
                    return@with
                }
                val keyword = searchState.data.keyword
                _viewState.value = SearchViewState(
                    searchState = Success(searchState.data.copy(showReloadNextPage = false, nextPageLoading = true)),
                    keyword = viewState.value.keyword,
                )
                searchJob = viewModelScope.launch {
                    try {
                        val nextPageMovieResult = searchMoviesUseCase.execute(keyword, nextPage)
                        _viewState.value = SearchViewState(
                            searchState = Success(
                                searchState.data.copy(
                                    movies = fetchedMovies + nextPageMovieResult.movies,
                                    page = nextPage,
                                    showReloadNextPage = false,
                                    nextPageLoading = false,
                                )
                            ),
                            keyword = viewState.value.keyword,
                        )
                    } catch (ignored: CancellationException) {
                        throw ignored
                    } catch (e: Throwable) {
                        _viewState.value = SearchViewState(
                            searchState = Success(
                                searchState.data.copy(
                                    showReloadNextPage = true,
                                    nextPageLoading = false,
                                )
                            ),
                            keyword = viewState.value.keyword,
                        )
                    }
                }
            }
        }
    }

    fun onItemClick(selectedMovie: String) {
        with(viewState.value.searchState) {
            when (this) {
                is Success -> {
                    viewModelScope.launch {
                        _navigationRequest.emit(
                            MovieInfoEvent(
                                data.movies,
                                selectedMovie
                            )
                        )
                    }
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

object MovieNotFoundException: RuntimeException()
