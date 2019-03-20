package leo.me.la.data

import io.mockk.Called
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.data.model.MovieDataModel
import leo.me.la.data.model.MovieSearchResultDataModel
import leo.me.la.data.source.MovieCacheDataSource
import leo.me.la.data.source.MovieRemoteDataSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MovieRepositoryImplTest {
    private val movieRemoteDataSource: MovieRemoteDataSource = mockk()
    private val movieCacheDataSource: MovieCacheDataSource = mockk()
    private val searchRepository = MovieRepositoryImpl(movieRemoteDataSource, movieCacheDataSource)

    @Test
    fun `should get correct movies if remote data source fetches successfully`() {
        val desiredResult = MovieSearchResult(
            listOf(
                Movie(
                    "Batman Begins",
                    "2005",
                    "tt0372784",
                    MovieType.Movie,
                    "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
                ),
                Movie(
                    "Batman: The Animated Series",
                    "1992–1995",
                    "tt0103359",
                    MovieType.Series,
                    "https://m.media-amazon.com/images/M/MV5BOTM3MTRkZjQtYjBkMy00YWE1LTkxOTQtNDQyNGY0YjYzNzAzXkEyXkFqcGdeQXVyOTgwMzk1MTA@._V1_SX300.jpg"
                ),
                Movie(
                    "Batman: The Dark Knight Returns, Part 1",
                    "2012",
                    "tt2313197",
                    MovieType.Other,
                    "https://m.media-amazon.com/images/M/MV5BMzIxMDkxNDM2M15BMl5BanBnXkFtZTcwMDA5ODY1OQ@@._V1_SX300.jpg"
                )
            ),
            3
        )
        runBlocking {
            coEvery {
                movieRemoteDataSource.searchMoviesByKeyword("Batman", 2)
            } returns MovieSearchResultDataModel(
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
                        "game",
                        "https://m.media-amazon.com/images/M/MV5BMzIxMDkxNDM2M15BMl5BanBnXkFtZTcwMDA5ODY1OQ@@._V1_SX300.jpg"
                    )
                ),
                3
            )
            val actualResult = searchRepository.searchMoviesByKeyword("Batman", 2)
            assertThat(actualResult).isEqualTo(desiredResult)
            coVerify { movieRemoteDataSource.searchMoviesByKeyword("Batman", 2) }
        }
    }

    @Test(expected = Exception::class)
    fun `should propagate exception if remote data source raises one`() {
        runBlocking {
            coEvery {
                movieRemoteDataSource.searchMoviesByKeyword("Batman", 2)
            } throws Exception()
            searchRepository.searchMoviesByKeyword("Batman", 2)
        }
    }

    @Test
    fun `should give data from cache if it is not stale`() {
        runBlocking {
            coEvery {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
            } returns Pair(
                MovieDataModel(
                    "Batman",
                    "2005",
                    "tt12345",
                    "movie"
                ),
                System.currentTimeMillis() - 4 * 60 * 1000 // 4 minutes before
            )
            val result = searchRepository.searchMovieByImdbId("tt12345")
            assertThat(result).isEqualTo(
                Movie(
                    "Batman",
                    "2005",
                    "tt12345",
                    MovieType.Movie
                )
            )
            coVerifyAll {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
                movieRemoteDataSource wasNot Called
            }
        }
    }

    @Test
    fun `should fetch data from remote if it is stale`() {
        runBlocking {
            coEvery {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
            } returns Pair(
                MovieDataModel(
                    "Batman",
                    "2005",
                    "tt12345",
                    "movie"
                ),
                System.currentTimeMillis() - 6 * 60 * 1000 // 6 minutes before
            )
            coEvery {
                movieCacheDataSource.saveMovie(any())
            } just Runs
            coEvery {
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            } returns MovieDataModel(
                "Batman remote",
                "2005",
                "tt12345",
                "movie"
            )
            val result = searchRepository.searchMovieByImdbId("tt12345")
            assertThat(result).isEqualTo(
                Movie(
                    "Batman remote",
                    "2005",
                    "tt12345",
                    MovieType.Movie
                )
            )
            coVerifyAll {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
                movieCacheDataSource.saveMovie(any())
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            }
        }
    }

    @Test
    fun `should fetch data from remote if it cache throws`() {
        runBlocking {
            coEvery {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
            } throws Throwable()
            coEvery {
                movieCacheDataSource.saveMovie(any())
            } just Runs
            coEvery {
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            } returns MovieDataModel(
                "Batman remote",
                "2005",
                "tt12345",
                "movie"
            )
            val result = searchRepository.searchMovieByImdbId("tt12345")
            assertThat(result).isEqualTo(
                Movie(
                    "Batman remote",
                    "2005",
                    "tt12345",
                    MovieType.Movie
                )
            )
            coVerifyAll {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
                movieCacheDataSource.saveMovie(any())
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            }
        }
    }

    @Test
    fun `should fetch data from remote if it cache returns null`() {
        runBlocking {
            coEvery {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
            } returns null
            coEvery {
                movieCacheDataSource.saveMovie(any())
            } just Runs
            coEvery {
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            } returns MovieDataModel(
                "Batman remote",
                "2005",
                "tt12345",
                "movie"
            )
            val result = searchRepository.searchMovieByImdbId("tt12345")
            assertThat(result).isEqualTo(
                Movie(
                    "Batman remote",
                    "2005",
                    "tt12345",
                    MovieType.Movie
                )
            )
            coVerifyAll {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
                movieCacheDataSource.saveMovie(any())
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            }
        }
    }

    @Test
    fun `should fetch data from remote successfully even if saving fails`() {
        runBlocking {
            coEvery {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
            } returns null
            coEvery {
                movieCacheDataSource.saveMovie(any())
            } throws Throwable()
            coEvery {
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            } returns MovieDataModel(
                "Batman remote",
                "2005",
                "tt12345",
                "movie"
            )
            val result = searchRepository.searchMovieByImdbId("tt12345")
            assertThat(result).isEqualTo(
                Movie(
                    "Batman remote",
                    "2005",
                    "tt12345",
                    MovieType.Movie
                )
            )
            coVerifyAll {
                movieCacheDataSource.loadMovieByImdbId("tt12345")
                movieCacheDataSource.saveMovie(any())
                movieRemoteDataSource.searchMoviesByImdbId("tt12345")
            }
        }
    }
}
