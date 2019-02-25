package leo.me.la.remote

import kotlinx.coroutines.runBlocking
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import okhttp3.mockwebserver.MockResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import retrofit2.HttpException

internal class MovieRemoteDataSourceImplTest : BaseApiTest(){
    private val omdbRestApi = getMockedRestApi<OmdbRestApi>()

    private val movieRemoteDataSource = MovieRemoteDataSourceImpl(omdbRestApi)

    @Test
    fun `should map successfully to application model if response is parsed successfully`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setBody("json/search-result.json".readFileContent())
                    .setResponseCode(200)
            )
            val result = movieRemoteDataSource.searchMoviesByKeyword("Batman")

            assertThat(result.movies.size).isEqualTo(3)
            assertThat(result.movies).isEqualTo(
                listOf(
                    Movie(
                        "Batman Begins",
                        "2005",
                        MovieType.Movie,
                        "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
                    ),
                    Movie(
                        "Batman: The Animated Series",
                        "1992–1995",
                        MovieType.Series,
                        "https://m.media-amazon.com/images/M/MV5BOTM3MTRkZjQtYjBkMy00YWE1LTkxOTQtNDQyNGY0YjYzNzAzXkEyXkFqcGdeQXVyOTgwMzk1MTA@._V1_SX300.jpg"
                    ),
                    Movie(
                        "Batman: The Dark Knight Returns, Part 1",
                        "2012",
                        MovieType.Other,
                        "https://m.media-amazon.com/images/M/MV5BMzIxMDkxNDM2M15BMl5BanBnXkFtZTcwMDA5ODY1OQ@@._V1_SX300.jpg"
                    )
                )
            )
            assertThat(result.totalResults).isEqualTo(3)
        }
    }

    @Test(expected = HttpException::class)
    fun `should propagate Exception if it happens`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setResponseCode(404)
            )
            movieRemoteDataSource.searchMoviesByKeyword("Batman")
        }
    }
}
