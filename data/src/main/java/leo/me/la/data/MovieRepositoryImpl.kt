package leo.me.la.data

import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
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
            .let { result ->
                MovieSearchResult(
                    result.movies.map { it.toMovie() },
                    result.totalResults
                )
            }
    }
    private fun String.splitToList() : List<String> {
        return this.split(", ")
    }

    private fun MovieDataModel.toMovie() : Movie {
        return Movie(
            title,
            year,
            imdbId,
            when(type) {
                "movie" -> MovieType.Movie
                "series" -> MovieType.Series
                else -> MovieType.Other
            },
            poster,
            rated,
            released,
            runtime,
            genres?.splitToList(),
            directors?.splitToList(),
            writers?.splitToList(),
            actors?.splitToList(),
            plot,
            languages?.splitToList(),
            country,
            awards,
            metaScore,
            imdbRating,
            imdbVotes,
            boxOffice,
            dvdRelease,
            production,
            website
        )
    }
}
