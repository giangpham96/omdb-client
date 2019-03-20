package leo.me.la.cache.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieCacheModel(
    @PrimaryKey @ColumnInfo(name = "imdb_id") val imdbId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "poster") val poster: String? = null,
    @ColumnInfo(name = "rated") val rated: String? = null,
    @ColumnInfo(name = "released_at") val released: String? = null,
    @ColumnInfo(name = "runtime") val runtime: String? = null,
    @ColumnInfo(name = "genres") val genres: String? = null,
    @ColumnInfo(name = "directors") val directors: String? = null,
    @ColumnInfo(name = "writers") val writers: String? = null,
    @ColumnInfo(name = "actors") val actors: String? = null,
    @ColumnInfo(name = "plot") val plot: String? = null,
    @ColumnInfo(name = "languages") val languages: String? = null,
    @ColumnInfo(name = "countries") val countries: String? = null,
    @ColumnInfo(name = "awards") val awards: String? = null,
    @ColumnInfo(name = "meta_score") val metaScore: Int? = null,
    @ColumnInfo(name = "imdb_rating") val imdbRating: Double? = null,
    @ColumnInfo(name = "imdb_votes") val imdbVotes: Int? = null,
    @ColumnInfo(name = "box_office") val boxOffice: String? = null,
    @ColumnInfo(name = "dvd_released_at") val dvdRelease: String? = null,
    @ColumnInfo(name = "production") val production: String? = null,
    @ColumnInfo(name = "website") val website: String? = null,
    @ColumnInfo(name = "recorded_at") val recordedAt: Long = System.currentTimeMillis()
)
