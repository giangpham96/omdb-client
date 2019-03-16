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
            .let {
                MovieSearchResult(
                    it.movies.map {
                        Movie(
                            it.title,
                            it.year,
                            it.imdbId,
                            when(it.type) {
                                "movie" -> MovieType.Movie
                                "series" -> MovieType.Series
                                else -> MovieType.Other
                            },
                            it.poster,
                            it.rated,
                            it.released,
                            it.runtime,
                            it.genres?.splitToList(),
                            it.directors?.splitToList(),
                            it.writers?.splitToList(),
                            it.actors?.splitToList(),
                            it.plot,
                            it.languages?.splitToList(),
                            it.country,
                            it.awards,
                            it.metaScore,
                            it.imdbRating,
                            it.imdbVotes,
                            it.boxOffice,
                            it.dvdRelease,
                            it.production,
                            it.website
                        )
                    },
                    it.totalResults
                )
            }
    }
    private fun String.splitToList() : List<String> {
        return this.split(", ")
    }
}
