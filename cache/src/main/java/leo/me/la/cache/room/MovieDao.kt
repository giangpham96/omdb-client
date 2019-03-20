package leo.me.la.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
internal interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg movies: MovieCacheModel)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(vararg movies: MovieCacheModel)

    @Query("SELECT * FROM movie WHERE imdb_id = :imdbId LIMIT 1")
    suspend fun getMovieByImdbId(imdbId: String) : MovieCacheModel?

    @Transaction
    suspend fun insertOrUpdate(vararg movies: MovieCacheModel) {
        insert(*movies)
        update(*movies)
    }
}
