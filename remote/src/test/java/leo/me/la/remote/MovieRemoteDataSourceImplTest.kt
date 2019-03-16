package leo.me.la.remote

import kotlinx.coroutines.runBlocking
import leo.me.la.data.model.MovieDataModel
import okhttp3.mockwebserver.MockResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import retrofit2.HttpException
import java.rmi.UnexpectedException

internal class MovieRemoteDataSourceImplTest : BaseApiTest(){
    private val omdbRestApi = getMockedRestApi<OmdbRestApi>()

    private val movieRemoteDataSource = MovieRemoteDataSourceImpl(omdbRestApi)

    @Test
    fun `should map successfully to search model if response is parsed successfully`() {
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
                    MovieDataModel(
                        "Batman Begins",
                        "2005",
                        "tt0372784",
                        "movie",
                        "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
                    ),
                    MovieDataModel(
                        "Batman: The Animated Series",
                        "1992–1995",
                        "tt0103359",
                        "series",
                        "https://m.media-amazon.com/images/M/MV5BOTM3MTRkZjQtYjBkMy00YWE1LTkxOTQtNDQyNGY0YjYzNzAzXkEyXkFqcGdeQXVyOTgwMzk1MTA@._V1_SX300.jpg"
                    ),
                    MovieDataModel(
                        "Batman: The Dark Knight Returns, Part 1",
                        "2012",
                        "tt2313197",
                        "cartoon",
                        "N/A"
                    )
                )
            )
            assertThat(result.totalResults).isEqualTo(3)
        }
    }

    @Test
    fun `should map successfully to movie model if response is parsed successfully`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setBody("json/imdb-id-search-result.json".readFileContent())
                    .setResponseCode(200)
            )
            val result = movieRemoteDataSource.searchMoviesByImdbId("tt4633694")

            assertThat(result).isEqualTo(
                MovieDataModel(
                    "Spider-Man: Into the Spider-Verse",
                    "2018",
                    "tt4633694",
                    "movie",
                    "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_SX300.jpg",
                    "PG",
                    "14 Dec 2018",
                    "117 min",
                    "Animation, Action, Adventure, Family, Sci-Fi",
                    "Bob Persichetti, Peter Ramsey, Rodney Rothman",
                    "Phil Lord (screenplay by), Rodney Rothman (screenplay by), Phil Lord (story by)",
                    "Shameik Moore, Jake Johnson, Hailee Steinfeld, Mahershala Ali",
                    "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                            "five counterparts from other dimensions to stop a threat for all realities.",
                    "English, Spanish",
                    "USA",
                    null,
                    87,
                    8.6,
                    122126,
                    null,
                    "26 Feb 2019",
                    "Sony Pictures",
                    "http://www.intothespiderverse.movie/"
                )
            )
        }
    }

    @Test(expected = HttpException::class)
    fun `should propagate Exception if it happens when search movies by keyword`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setResponseCode(404)
            )
            movieRemoteDataSource.searchMoviesByKeyword("Batman")
        }
    }

    @Test(expected = HttpException::class)
    fun `should propagate Exception if it happens when search movie by imdb id`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setResponseCode(403)
            )
            movieRemoteDataSource.searchMoviesByImdbId("tt4633694")
        }
    }

    @Test(expected = UnexpectedException::class)
    fun `should unwrap JsonDataException if it happens when search movies by keyword`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{}")
            )
            movieRemoteDataSource.searchMoviesByKeyword("Batman")
        }
    }

    @Test(expected = UnexpectedException::class)
    fun `should unwrap JsonDataException if it happens when search movie by imdb id`() {
        runBlocking {
            mockServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{}")
            )
            movieRemoteDataSource.searchMoviesByImdbId("tt4633694")
        }
    }
}
