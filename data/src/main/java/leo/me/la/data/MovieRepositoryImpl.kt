package leo.me.la.data

import leo.me.la.common.model.MovieSearchResult
import leo.me.la.data.source.MovieRemoteDataSource
import leo.me.la.domain.repository.MovieRepository

internal class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource
) : MovieRepository {
    override suspend fun searchMoviesByKeyword(
        keyword: String,
        page: Int
    ): MovieSearchResult {
        return movieRemoteDataSource.searchMoviesByKeyword(keyword, page)
    }
}
