package leo.me.la.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.domain.repository.MovieRepository

internal class SearchMoviesUseCaseImpl(
    private val movieRepository: MovieRepository
) : SearchMoviesUseCase {
    override suspend fun execute(
        keyword: String,
        page: Int
    ): MovieSearchResult {
        return withContext(Dispatchers.IO) {
            movieRepository.searchMoviesByKeyword(keyword, page)
        }
    }
}

interface SearchMoviesUseCase {
    suspend fun execute(
        keyword: String,
        page: Int = 1
    ): MovieSearchResult
}
