package leo.me.la.common.model

data class Movie(
    val title: String,
    val year: String,
    val imdbId: String,
    val type: MovieType,
    val poster: String
)

data class MovieSearchResult(
    val movies: List<Movie>,
    val totalResults: Int
)

enum class MovieType {
    Movie,
    Series,
    Other
}
