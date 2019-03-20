package leo.me.la.cache

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import leo.me.la.cache.room.MovieCacheModel
import leo.me.la.cache.room.MovieDao
import leo.me.la.data.model.MovieDataModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MovieCacheDataSourceImplTest {
    private val dao: MovieDao = mockk()
    private val dataSource = MovieCacheDataSourceImpl(dao)

    @Test
    fun `should return desired item if it exists`() {
        coEvery {
            dao.getMovieByImdbId("tt12345")
        } returns MovieCacheModel(
            "tt12345",
            "Batman",
            "2005",
            "movie",
            recordedAt = 1430000000000
        )

        runBlocking {
            val result = dataSource.loadMovieByImdbId("tt12345")
            assertThat(result).isEqualTo(
                Pair(
                    MovieDataModel(
                        "Batman",
                        "2005",
                        "tt12345",
                        "movie"
                    ),
                    1430000000000
                )
            )
            coVerify(exactly = 1) {
                dao.getMovieByImdbId("tt12345")
            }
        }
    }

    @Test
    fun `should return null if item is not cached`() {
        coEvery {
            dao.getMovieByImdbId(any())
        } returns null

        runBlocking {
            val result = dataSource.loadMovieByImdbId("tt12345")
            assertThat(result).isNull()
            coVerify(exactly = 1) {
                dao.getMovieByImdbId("tt12345")
            }
        }
    }

    @Test
    fun `should call DAO's save function`() {
        coEvery {
            dao.insertOrUpdate(*anyVararg())
        } just Runs

        runBlocking {
            dataSource.saveMovie(
                MovieDataModel(
                    "Batman",
                    "2005",
                    "tt12345",
                    "movie"
                )
            )
        }
        coVerify(exactly = 1) {
            dao.insertOrUpdate(
                *varargAny {
                    if (position == 0) {
                        it.title == "Batman"
                                && it.year == "2005"
                                && it.imdbId == "tt12345"
                                && it.type == "movie"
                    } else false
                }
            )
        }
    }
}
