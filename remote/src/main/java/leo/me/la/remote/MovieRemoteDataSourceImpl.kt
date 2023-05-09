package leo.me.la.remote

import com.squareup.moshi.JsonDataException
import leo.me.la.data.model.MovieDataModel
import leo.me.la.data.model.MovieSearchResultDataModel
import leo.me.la.data.source.MovieRemoteDataSource
import leo.me.la.remote.model.MovieRemoteModel

internal class MovieRemoteDataSourceImpl(
    private val omdbRestApi: OmdbRestApi
) : MovieRemoteDataSource {
    override suspend fun searchMoviesByImdbId(imdbId: String): MovieDataModel {
        return try {
            omdbRestApi
                .searchByImdbIdAsync(imdbId)
                .let {
                    mapMovieRemoteModelToMovieDataModel(it)
                }
        } catch (e: JsonDataException) {
            throw e.cause ?: e
        }
    }

    override suspend fun searchMoviesByKeyword(
        keyword: String,
        page: Int
    ): MovieSearchResultDataModel {
        return try {
            omdbRestApi
                .searchByKeywordAsync(keyword, page)
                .let {
                    MovieSearchResultDataModel(
                        it.movies.map { movie ->
                            mapMovieRemoteModelToMovieDataModel(movie)
                        },
                        it.totalResults
                    )
                }
        } catch (e: JsonDataException) {
            throw e.cause ?: e
        }
    }

    private fun mapMovieRemoteModelToMovieDataModel(
        movieRemoteModel: MovieRemoteModel
    ): MovieDataModel {
        return MovieDataModel(
            movieRemoteModel.title,
            movieRemoteModel.year,
            movieRemoteModel.imdbId,
            movieRemoteModel.type,
            movieRemoteModel.poster,
            movieRemoteModel.rated?.simplifyRemoteField(),
            movieRemoteModel.released?.simplifyRemoteField(),
            movieRemoteModel.runtime?.simplifyRemoteField(),
            movieRemoteModel.genres?.simplifyRemoteField(),
            movieRemoteModel.directors?.simplifyRemoteField(),
            movieRemoteModel.writers?.simplifyRemoteField(),
            movieRemoteModel.actors?.simplifyRemoteField(),
            movieRemoteModel.plot?.simplifyRemoteField(),
            movieRemoteModel.languages?.simplifyRemoteField(),
            movieRemoteModel.countries?.simplifyRemoteField(),
            movieRemoteModel.awards?.simplifyRemoteField(),
            try {
                movieRemoteModel.metaScore?.toInt()
            } catch (t: Throwable) {
                null
            },
            try {
                movieRemoteModel.imdbRating?.toDouble()
            } catch (t: Throwable) {
                null
            },
            try {
                movieRemoteModel.imdbVotes?.replace(",", "")?.toInt()
            } catch (t: Throwable) {
                null
            },
            movieRemoteModel.boxOffice?.simplifyRemoteField(),
            movieRemoteModel.dvdRelease?.simplifyRemoteField(),
            movieRemoteModel.production?.simplifyRemoteField(),
            movieRemoteModel.website?.simplifyRemoteField()
        )
    }

    private fun String.simplifyRemoteField(): String? {
        return if (this == "N/A") null else this
    }
}
