package leo.me.la.remote

import kotlinx.coroutines.Deferred
import leo.me.la.remote.model.RemoteMovieSearchModel
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OmdbRestApi {
    @GET("/")
    fun searchByKeywords(
        @Query("s")
        keyword: String,
        @Query("page")
        page: Int = 1
    ) : Deferred<RemoteMovieSearchModel>
}
