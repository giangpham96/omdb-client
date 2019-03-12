package leo.me.la.domain

import leo.me.la.common.model.MovieSearchResult
import leo.me.la.domain.repository.MovieRepository

internal class SearchMoviesUseCaseImpl(
    private val movieRepository: MovieRepository
) : SearchMoviesUseCase {
    override suspend fun execute(
        keyword: String,
        page: Int
    ): MovieSearchResult {
        return movieRepository.searchMoviesByKeyword(keyword, page)
    }
}

interface SearchMoviesUseCase {
    suspend fun execute(
        keyword: String,
        page: Int = 1
    ): MovieSearchResult
}
