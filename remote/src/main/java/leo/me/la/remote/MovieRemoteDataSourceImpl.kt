package leo.me.la.remote

import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.data.source.MovieRemoteDataSource

internal class MovieRemoteDataSourceImpl(
    private val omdbRestApi: OmdbRestApi
) : MovieRemoteDataSource {
    override suspend fun searchMoviesByKeyword(keyword: String, page: Int): MovieSearchResult {
        return omdbRestApi.searchByKeywords(keyword, page).await()
            .let {
                MovieSearchResult(
                    it.result.map { movie ->
                        Movie(
                            movie.title,
                            movie.year,
                            movie.imdbId,
                            movie.type.let { type ->
                                when (type) {
                                    "movie" -> MovieType.Movie
                                    "series" -> MovieType.Series
                                    else -> MovieType.Other
                                }
                            },
                            movie.poster
                        )
                    },
                    it.totalResults
                )
            }
    }
}
