package leo.me.la.remote.model

data class MovieSearchResultRemoteModel(
    val movies: List<MovieRemoteModel>,
    val totalResults: Int
)

data class MovieRemoteModel(
    val title: String,
    val year: String,
    val imdbId: String,
    val type: String,
    val poster: String,
    val rated: String? = null,
    val released: String? = null,
    val runtime: String? = null,
    val genres: String? = null,
    val directors: String? = null,
    val writers: String? = null,
    val actors: String? = null,
    val plot: String? = null,
    val languages: String? = null,
    val countries: String? = null,
    val awards: String? = null,
    val metaScore: String? = null,
    val imdbRating: String? = null,
    val imdbVotes: String? = null,
    val boxOffice: String? = null,
    val dvdRelease: String? = null,
    val production: String? = null,
    val website: String? = null
)
