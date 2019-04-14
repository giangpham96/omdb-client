package leo.me.la.presentation

import leo.me.la.common.model.MovieRate
import leo.me.la.common.model.MovieType

sealed class MovieInfoViewState : BaseViewState {
    data class Loading(val poster: String?): MovieInfoViewState()
    data class LoadMovieInfoSuccess(
        val title: String,
        val type: MovieType,
        val poster: String?,
        val rated: MovieRate,
        val released: String,
        val runtime: String,
        val genres: List<String>,
        val directors: List<String>,
        val writers: String,
        val actors: List<String>,
        val plot: String,
        val languages: String,
        val countries: String,
        val awards: String,
        val metaScore: String,
        val imdbRating: String,
        val imdbVotes: String,
        val boxOffice: String,
        val dvdRelease: String,
        val production: String,
        val website: Pair<String, Boolean> // the boolean value indicates if the website text view is clickable
    ): MovieInfoViewState()
    data class LoadMovieInfoFailure(val reason: Throwable): MovieInfoViewState()
}
