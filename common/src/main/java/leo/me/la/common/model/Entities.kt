package leo.me.la.common.model

data class Movie(
    val title: String,
    val year: String,
    val imdbId: String,
    val type: MovieType,
    val poster: String? = null,
    val rated: String? = null,
    val released: String? = null,
    val runtime: String? = null,
    val genres: List<String>? = null,
    val directors: List<String>? = null,
    val writers: List<String>? = null,
    val actors: List<String>? = null,
    val plot: String? = null,
    val languages: List<String>? = null,
    val country: String? = null,
    val awards: String? = null,
    val metaScore: Int? = null,
    val imdbRating: Double? = null,
    val imdbVotes: Int? = null,
    val boxOffice: String? = null,
    val dvdRelease: String? = null,
    val production: String? = null,
    val website: String? = null
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
