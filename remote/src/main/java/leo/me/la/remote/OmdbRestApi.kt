package leo.me.la.remote

import kotlinx.coroutines.Deferred
import leo.me.la.remote.model.MovieRemoteModel
import leo.me.la.remote.model.MovieSearchResultRemoteModel
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OmdbRestApi {
    @GET("/")
    fun searchByKeyword(
        @Query("s")
        keyword: String,
        @Query("page")
        page: Int = 1
    ) : Deferred<MovieSearchResultRemoteModel>

    @GET("/")
    fun searchByImdbId(
        @Query("i")
        imdbId: String,
        @Query("plot")
        plot: String = "full"
    ) : Deferred<MovieRemoteModel>
}
