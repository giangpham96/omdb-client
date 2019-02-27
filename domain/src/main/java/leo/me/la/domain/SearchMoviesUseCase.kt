package leo.me.la.domain

import leo.me.la.common.model.MovieSearchResult
import leo.me.la.domain.repository.SearchRepository

class SearchMoviesUseCaseImpl(
    private val searchRepository: SearchRepository
) : SearchMoviesUseCase {
    override suspend fun execute(
        keyword: String,
        page: Int
    ): MovieSearchResult {
        return searchRepository.searchMoviesByKeyword(keyword, page)
    }
}

interface SearchMoviesUseCase {
    suspend fun execute(
        keyword: String,
        page: Int = 1
    ): MovieSearchResult
}
