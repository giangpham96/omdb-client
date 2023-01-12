package leo.me.la.cache.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieCacheModel::class],
    version = 1,
    exportSchema = false
)
internal abstract class OmdbDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
