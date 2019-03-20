package leo.me.la.cache

import leo.me.la.cache.room.MovieCacheModel
import leo.me.la.cache.room.MovieDao
import leo.me.la.data.model.MovieDataModel
import leo.me.la.data.source.MovieCacheDataSource

internal class MovieCacheDataSourceImpl(
    private val movieDao: MovieDao
) : MovieCacheDataSource {
    override suspend fun loadMovieByImdbId(imdbId: String): Pair<MovieDataModel, Long>? {
        return movieDao.getMovieByImdbId(imdbId)?.let {
            Pair(
                MovieDataModel(
                    it.title,
                    it.year,
                    it.imdbId,
                    it.type,
                    it.poster,
                    it.rated,
                    it.released,
                    it.runtime,
                    it.genres,
                    it.directors,
                    it.writers,
                    it.actors,
                    it.plot,
                    it.languages,
                    it.countries,
                    it.awards,
                    it.metaScore,
                    it.imdbRating,
                    it.imdbVotes,
                    it.boxOffice,
                    it.dvdRelease,
                    it.production,
                    it.website
                ),
                it.recordedAt
            )
        }
    }

    override suspend fun saveMovie(movie: MovieDataModel) {
        movieDao.insertOrUpdate(
            MovieCacheModel(
                imdbId = movie.imdbId,
                title = movie.title,
                year = movie.year,
                type = movie.type,
                poster = movie.poster,
                rated = movie.rated,
                released = movie.released,
                runtime = movie.runtime,
                genres = movie.genres,
                directors = movie.directors,
                writers = movie.writers,
                actors = movie.actors,
                plot = movie.plot,
                languages = movie.languages,
                countries = movie.country,
                awards = movie.awards,
                metaScore = movie.metaScore,
                imdbRating = movie.imdbRating,
                imdbVotes = movie.imdbVotes,
                boxOffice = movie.boxOffice,
                dvdRelease = movie.dvdRelease,
                production = movie.production,
                website = movie.website
            )
        )
    }
}
