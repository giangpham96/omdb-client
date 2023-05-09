package leo.me.la.remote

import leo.me.la.remote.model.MovieRemoteModel
import leo.me.la.remote.model.MovieSearchResultRemoteModel
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OmdbRestApi {
    @GET("/")
    suspend fun searchByKeywordAsync(
        @Query("s")
        keyword: String,
        @Query("page")
        page: Int = 1
    ) : MovieSearchResultRemoteModel

    @GET("/")
    suspend fun searchByImdbIdAsync(
        @Query("i")
        imdbId: String,
        @Query("plot")
        plot: String = "full"
    ) : MovieRemoteModel
}
