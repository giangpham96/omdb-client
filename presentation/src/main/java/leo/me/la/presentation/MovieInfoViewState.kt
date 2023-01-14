package leo.me.la.presentation

import leo.me.la.common.model.MovieRate
import leo.me.la.common.model.MovieType

data class MovieInfoViewState(
    val state: DataState<MovieInfo>
) {
    data class MovieInfo(
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
    )
}
