package leo.me.la.domain

import leo.me.la.common.model.Movie
import leo.me.la.domain.repository.MovieRepository

interface LoadMovieInfoUseCase {
    suspend fun execute(imdbId: String): Movie
}

internal class LoadMovieInfoUseCaseImpl(
    private val movieRepository: MovieRepository
) : LoadMovieInfoUseCase {
    override suspend fun execute(imdbId: String): Movie {
        return movieRepository.searchMovieByImdbId(imdbId)
    }
}
