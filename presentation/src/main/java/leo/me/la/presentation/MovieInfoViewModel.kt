package leo.me.la.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import leo.me.la.common.toFlagEmoji
import leo.me.la.domain.LoadMovieInfoUseCase
import leo.me.la.presentation.DataState.Loading
import leo.me.la.presentation.MovieInfoViewState.MovieInfo

class MovieInfoViewModel(
    private val loadMovieInfoUseCase: LoadMovieInfoUseCase,
    imdbId: String,
) : BaseViewModel<MovieInfoViewState>() {

    override val _viewState = MutableStateFlow(MovieInfoViewState(Loading))
    init {
        loadMovieInfo(imdbId)
    }

    private fun loadMovieInfo(imdb: String) {
        viewModelScope.launch {
            try {
                val movie = loadMovieInfoUseCase.execute(imdb)
                _viewState.value = MovieInfoViewState(
                    DataState.Success(
                        MovieInfo(
                            title = movie.title,
                            type = movie.type,
                            rated = movie.rated,
                            released = movie.released ?: "Unknown",
                            runtime = movie.runtime ?: "Unknown runtime",
                            genres = movie.genres ?: emptyList(),
                            directors = movie.directors ?: emptyList(),
                            writers = movie.writers?.joinNames() ?: "Unknown",
                            actors = movie.actors ?: emptyList(),
                            plot = movie.plot ?: "Unknown",
                            languages = movie.languages?.joinNames() ?: "",
                            countries = movie.countries
                                ?.mapNotNull {
                                    it.toFlagEmoji()
                                }
                                ?.joinToString(" ") ?: "Unknown",
                            awards = movie.awards ?: "None",
                            metaScore = movie.metaScore?.toString()?.let { "$it/100" } ?: "???/100",
                            imdbRating = movie.imdbRating?.toString()?.let { "$it/10 IMDb" }
                                ?: "???/10 IMDb",
                            imdbVotes = movie.imdbVotes?.toString()?.let { "$it votes" }
                                ?: "??? votes",
                            boxOffice = movie.boxOffice ?: "Unknown",
                            dvdRelease = movie.dvdRelease ?: "Unknown",
                            production = movie.production ?: "Unknown",
                        ))
                )
            } catch (t: Throwable) {
                _viewState.value = MovieInfoViewState(DataState.Failure(t))
            }
        }
    }

    private fun List<String>.joinNames(): String {
        return when {
            size > 1 -> listOf(
                subList(0, size - 1).joinToString(", "),
                last()
            ).joinToString(" and ")

            size == 1 -> first()
            else -> "Unknown"
        }
    }
}
