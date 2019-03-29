package leo.me.la.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import leo.me.la.common.toFlagEmoji
import leo.me.la.domain.LoadMovieInfoUseCase
import kotlin.coroutines.CoroutineContext

class MovieInfoViewModel(
    private val loadMovieInfoUseCase: LoadMovieInfoUseCase,
    context: CoroutineContext = Dispatchers.Main,
    imdbId: String
) : BaseViewModel<MovieInfoViewState>(context) {
    init {
        _viewStates.value = MovieInfoViewState.Loading
        loadMovieInfo(imdbId)
    }

    private fun loadMovieInfo(imdb: String) {
        launch {
            try {
                val movie = loadMovieInfoUseCase.execute(imdb)
                _viewStates.value = MovieInfoViewState.LoadMovieInfoSuccess(
                    movie.title,
                    movie.type,
                    movie.poster,
                    movie.rated,
                    movie.released ?: "Unknown",
                    movie.runtime ?: "Unknown runtime",
                    movie.genres?.joinNames() ?: "Unknown",
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
                    movie.metaScore?.toString() ?: "?",
                    movie.imdbRating?.toString() ?: "?",
                    movie.imdbVotes?.toString() ?: "?",
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
        return listOf(
            subList(0, size - 1).joinToString(", "),
            last()
        ).joinToString(" and ")
    }
}
