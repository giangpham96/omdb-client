package leo.me.la.domain.repository

import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult

interface MovieRepository {
    suspend fun searchMoviesByKeyword(keyword: String, page: Int): MovieSearchResult
    suspend fun searchMovieByImdbId(imdbId: String): Movie
}
