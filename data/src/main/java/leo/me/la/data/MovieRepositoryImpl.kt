package leo.me.la.data

import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieRate
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.data.model.MovieDataModel
import leo.me.la.data.source.MovieCacheDataSource
import leo.me.la.data.source.MovieRemoteDataSource
import leo.me.la.domain.repository.MovieRepository

internal class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieCacheDataSource: MovieCacheDataSource
) : MovieRepository {

    override suspend fun searchMovieByImdbId(imdbId: String): Movie {
        val movieCacheModel = try {
            movieCacheDataSource.loadMovieByImdbId(imdbId)
        } catch (ignored: Throwable) {
            null
        }

        val movieDataModel = movieCacheModel?.let {
            if(System.currentTimeMillis() - it.second > 5 * 60 * 1000) {
                fetchMovieFromRemoteAndSaveToCache(imdbId)
            } else {
                it.first
            }
        } ?: fetchMovieFromRemoteAndSaveToCache(imdbId)

        return movieDataModel.toMovie()
    }

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

    private suspend fun fetchMovieFromRemoteAndSaveToCache(imdbId: String) : MovieDataModel {
        return movieRemoteDataSource.searchMoviesByImdbId(imdbId)
            .also {
                try {
                    movieCacheDataSource.saveMovie(it)
                } catch (ignored: Throwable) {
                    // Doesn't matter if something wrong happens when saving
                }
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
            rated?.let {
                try {
                    MovieRate.valueOf(it.toUpperCase().replace("-", "_"))
                } catch (ignored: Throwable) {
                    MovieRate.UNKNOWN
                }
            } ?: MovieRate.UNKNOWN,
            released,
            runtime,
            genres?.splitToList(),
            directors?.splitToList(),
            writers?.splitToList(),
            actors?.splitToList(),
            plot,
            languages?.splitToList(),
            country?.splitToList(),
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
