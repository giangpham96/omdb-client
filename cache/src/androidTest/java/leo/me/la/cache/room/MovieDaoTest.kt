package leo.me.la.cache.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
open class MovieDaoTest {
    private lateinit var db: OmdbDatabase
    private lateinit var movieDao: MovieDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            OmdbDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        movieDao = db.movieDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.run {
            clearAllTables()
            close()
        }
    }

    @Test
    fun shouldReturnNothing() {
        runBlocking {
            assertThat(movieDao.getMovieByImdbId("tt0372784")).isNull()
        }
    }

    @Test
    fun shouldInsertSuccessfully() {
        runBlocking {
            val testMovie1 = MovieCacheModel(
                "tt0372784",
                "Batman Begins",
                "2005",
                "movie"
            )
            val testMovie2 = MovieCacheModel(
                "tt0372785",
                "Batman Ends",
                "2007",
                "sequel"
            )
            movieDao.insertOrUpdate(
                testMovie1,
                testMovie2
            )
            assertThat(movieDao.getMovieByImdbId("tt0372784")).isEqualTo(testMovie1)
            assertThat(movieDao.getMovieByImdbId("tt0372785")).isEqualTo(testMovie2)
        }
    }

    @Test
    fun shouldUpdateSuccessfully() {
        runBlocking {
            val testMovie = MovieCacheModel(
                "tt0372784",
                "Batman Begins",
                "2005",
                "movie"
            )
            val updatedMovie = MovieCacheModel(
                "tt0372784",
                "Batman Begins",
                "2005",
                "movie"
            )
            movieDao.insertOrUpdate(
                testMovie
            )
            assertThat(movieDao.getMovieByImdbId("tt0372784")).isEqualTo(testMovie)
            movieDao.insertOrUpdate(updatedMovie)
            assertThat(movieDao.getMovieByImdbId("tt0372784")).isEqualTo(updatedMovie)
        }
    }
}
