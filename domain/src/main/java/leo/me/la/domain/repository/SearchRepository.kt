package leo.me.la.domain.repository

import leo.me.la.common.model.MovieSearchResult

interface SearchRepository {
    suspend fun searchMoviesByKeyword(keyword: String, page: Int) : MovieSearchResult
}
