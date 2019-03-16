package leo.me.la.data.source

import leo.me.la.data.model.MovieDataModel
import leo.me.la.data.model.MovieSearchResultDataModel

interface MovieRemoteDataSource {
    /**
     * fetch movies from remote data source given keyword
     * @param keyword the desired keyword (in the movie title)
     * @param page
     */
    suspend fun searchMoviesByKeyword(
        keyword: String,
        page: Int = 1
    ) : MovieSearchResultDataModel

    /**
     * fetch a movie from remote data source given its imdb id
     * @param imdbId
     */
    suspend fun searchMoviesByImdbId(
        imdbId: String
    ) : MovieDataModel
}
