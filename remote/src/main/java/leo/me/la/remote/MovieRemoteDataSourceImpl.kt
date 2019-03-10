package leo.me.la.remote

import com.squareup.moshi.JsonDataException
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.data.source.MovieRemoteDataSource

internal class MovieRemoteDataSourceImpl(
    private val omdbRestApi: OmdbRestApi
) : MovieRemoteDataSource {
    override suspend fun searchMoviesByImdbId(imdbId: String): Movie {
        return try {
            omdbRestApi.searchByImdbId(imdbId).await()
        } catch (e: JsonDataException) {
            throw e.cause ?: e
        }
    }

    override suspend fun searchMoviesByKeyword(keyword: String, page: Int): MovieSearchResult {
        return try {
            omdbRestApi.searchByKeyword(keyword, page).await()
        } catch (e: JsonDataException) {
            throw e.cause ?: e
        }
    }
}
