package leo.me.la.data

import leo.me.la.common.model.MovieSearchResult
import leo.me.la.data.source.MovieRemoteDataSource
import leo.me.la.domain.repository.SearchRepository

internal class SearchRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource
) : SearchRepository {
    override suspend fun searchMoviesByKeyword(
        keyword: String,
        page: Int
    ): MovieSearchResult {
        return movieRemoteDataSource.searchMoviesByKeyword(keyword, page)
    }
}
