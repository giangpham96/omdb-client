package leo.me.la.data.model

data class MovieSearchResultDataModel(
    val movies: List<MovieDataModel>,
    val totalResults: Int
)

data class MovieDataModel(
    val title: String,
    val year: String,
    val imdbId: String,
    val type: String,
    val poster: String? = null,
    val rated: String? = null,
    val released: String? = null,
    val runtime: String? = null,
    val genres: String? = null,
    val directors: String? = null,
    val writers: String? = null,
    val actors: String? = null,
    val plot: String? = null,
    val languages: String? = null,
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
