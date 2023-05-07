package leo.me.la.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.domain.repository.MovieRepository

internal class SearchMoviesUseCaseImpl(
    private val movieRepository: MovieRepository
) : SearchMoviesUseCase {
    override suspend fun execute(
        keyword: String,
        page: Int
    ): Result<MovieSearchResult> = resultOf {
        withContext(Dispatchers.IO) {
            movieRepository.searchMoviesByKeyword(keyword, page)
        }
    }
}

interface SearchMoviesUseCase {
    suspend fun execute(
        keyword: String,
        page: Int = 1
    ): Result<MovieSearchResult>
}
