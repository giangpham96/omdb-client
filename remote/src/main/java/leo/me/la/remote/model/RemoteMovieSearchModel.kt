package leo.me.la.remote.model

data class RemoteMovieSearchModel(
    val result: List<RemoteMovieModel>,
    val totalResults: Int
)
