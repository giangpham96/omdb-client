package leo.me.la.cache

import androidx.room.Room
import leo.me.la.cache.room.OmdbDatabase
import leo.me.la.data.source.MovieCacheDataSource
import org.koin.dsl.module

val cacheModule = module {
    single {
        Room.databaseBuilder(
            get(),
            OmdbDatabase::class.java,
            "omdb.db"
        )
            .fallbackToDestructiveMigration() //Clear the db on migration - it's just a cache
            .build()
    }

    factory<MovieCacheDataSource> {
        MovieCacheDataSourceImpl(get())
    }

    factory {
        get<OmdbDatabase>().movieDao()
    }
}
