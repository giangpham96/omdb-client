package leo.me.la.data.source

import leo.me.la.data.model.MovieDataModel


interface MovieCacheDataSource {
    /**
     * fetch a movie from local SQLite database given its imdb id
     * @param imdbId
     */
    suspend fun loadMovieByImdbId(
        imdbId: String
    ) : Pair<MovieDataModel, Long>?

    /**
     * save a movie to local SQLite database
     */
    suspend fun saveMovie(movie: MovieDataModel)
}
