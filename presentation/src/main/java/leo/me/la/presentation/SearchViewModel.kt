package leo.me.la.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import leo.me.la.common.model.Movie
import leo.me.la.domain.SearchMoviesUseCase
import leo.me.la.exception.OmdbErrorException
import leo.me.la.presentation.DataState.Failure
import leo.me.la.presentation.DataState.Idle
import leo.me.la.presentation.DataState.Loading
import leo.me.la.presentation.DataState.Success
import leo.me.la.presentation.SearchViewState.SearchUi
import leo.me.la.presentation.SearchViewState.SearchUi.LoadingNextPage
import leo.me.la.presentation.SearchViewState.SearchUi.ReloadNextPage
import kotlin.math.ceil

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : BaseViewModel<SearchViewState>() {

    private var searchJob: Job? = null

    override val _viewState = MutableStateFlow(SearchViewState(Idle))

    private val _navigationRequest = MutableSharedFlow<MovieInfoEvent>()
    val navigationRequest: SharedFlow<MovieInfoEvent>
        get() = _navigationRequest

    fun searchMovies(keyword: String) {
        searchJob?.cancel()
        if (keyword.isEmpty()) {
            _viewState.value = viewState.value.copy(keyword = keyword, searchState = Idle)
            return
        }
        if (keyword.length < 3) {
            _viewState.value = viewState.value.copy(keyword = keyword)
            return
        }
        _viewState.value = viewState.value.copy(keyword = keyword, searchState = Loading)
        searchJob = viewModelScope.launch {
            delay(300)
            searchMoviesUseCase.execute(keyword)
                .onFailure { e ->
                    _viewState.value = when (e) {
                        is OmdbErrorException -> {
                            val err = if (e.message == "Movie not found!")
                                MovieNotFoundException
                            else
                                RuntimeException()
                            SearchViewState(Failure(err), keyword = keyword)
                        }

                        else -> SearchViewState(Failure(), keyword = keyword)
                    }
                }
                .onSuccess { result ->
                    _viewState.value = SearchViewState(
                        Success(
                            SearchUi(
                                keyword = keyword,
                                movies = result.movies.map(::convertMovie),
                                page = 1,
                                footer = null,
                                totalPages = ceil(result.totalResults.toFloat() / 10).toInt(),
                            )
                        ),
                        keyword = keyword,
                    )
                }
        }

    }

    fun loadNextPage() {
        with(viewState.value) {
            if (canLoadNextPage(searchState)) {
                val totalPages = searchState.data.totalPages
                val nextPage = searchState.data.page + 1
                val fetchedMovies = searchState.data.movies
                if (totalPages < nextPage || totalPages >= 100) {
                    return@with
                }
                val keyword = searchState.data.keyword
                _viewState.value = SearchViewState(
                    searchState = Success(
                        searchState.data.copy(footer = LoadingNextPage)
                    ),
                    keyword = viewState.value.keyword,
                )
                searchJob = viewModelScope.launch {
                    searchMoviesUseCase.execute(keyword, nextPage)
                        .map { it.movies.map(::convertMovie) }
                        .onFailure {
                            _viewState.value = SearchViewState(
                                searchState = Success(
                                    searchState.data.copy(footer =  ReloadNextPage)
                                ),
                                keyword = viewState.value.keyword,
                            )
                        }
                        .onSuccess {
                            _viewState.value = SearchViewState(
                                searchState = Success(
                                    searchState.data.copy(
                                        movies = fetchedMovies + it,
                                        page = nextPage,
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

    private fun convertMovie(movie: Movie): SearchUi.Movie {
        return SearchUi.Movie(movie.title, movie.poster, movie.imdbId)
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }
}

data class MovieInfoEvent(
    val movies: List<SearchUi.Movie>,
    val selectedMovie: String,
)

object MovieNotFoundException : RuntimeException()
