package leo.me.la.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import leo.me.la.common.toFlagEmoji
import leo.me.la.domain.LoadMovieInfoUseCase

class MovieInfoViewModel(
    private val loadMovieInfoUseCase: LoadMovieInfoUseCase,
    imdbId: String,
    poster: String?
) : BaseViewModel<MovieInfoViewState>() {
    init {
        _viewStates.value = MovieInfoViewState.Loading(poster)
        loadMovieInfo(imdbId)
    }

    private fun loadMovieInfo(imdb: String) {
        viewModelScope.launch {
            try {
                val movie = loadMovieInfoUseCase.execute(imdb)
                _viewStates.value = MovieInfoViewState.LoadMovieInfoSuccess(
                    movie.title,
                    movie.type,
                    movie.poster,
                    movie.rated,
                    movie.released ?: "Unknown",
                    movie.runtime ?: "Unknown runtime",
                    movie.genres ?: emptyList(),
                    movie.directors ?: emptyList(),
                    movie.writers?.joinNames() ?: "Unknown",
                    movie.actors ?: emptyList(),
                    movie.plot ?: "Unknown",
                    movie.languages?.joinNames() ?: "",
                    movie.countries
                        ?.mapNotNull {
                            it.toFlagEmoji()
                        }
                        ?.joinToString(" ") ?: "Unknown",
                    movie.awards ?: "None",
                    movie.metaScore?.toString()?.let { "$it/100" } ?: "???/100",
                    movie.imdbRating?.toString()?.let { "$it/10 IMDb" } ?: "???/10 IMDb",
                    movie.imdbVotes?.toString()?.let { "$it votes" } ?: "??? votes",
                    movie.boxOffice ?: "Unknown",
                    movie.dvdRelease ?: "Unknown",
                    movie.production ?: "Unknown",
                    movie.website?.let { Pair(it, true) } ?: Pair("Unknown", false)
                )
            } catch (t: Throwable) {
                _viewStates.value = MovieInfoViewState.LoadMovieInfoFailure(t)
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
