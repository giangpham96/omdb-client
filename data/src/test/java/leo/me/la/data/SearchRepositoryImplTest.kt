package leo.me.la.data

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.runBlocking
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.data.source.MovieRemoteDataSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SearchRepositoryImplTest {
    private val movieRemoteDataSource: MovieRemoteDataSource = mock()
    private val searchRepository = SearchRepositoryImpl(movieRemoteDataSource)

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
            whenever(
                movieRemoteDataSource.searchMoviesByKeyword("Batman", 2)
            ) doReturn desiredResult
            val actualResult = searchRepository.searchMoviesByKeyword("Batman", 2)
            assertThat(actualResult).isEqualTo(desiredResult)
            verify(movieRemoteDataSource).searchMoviesByKeyword("Batman", 2)
        }
    }
    @Test(expected = Exception::class)
    fun `should propagate exception if remote data source raises one`() {
        runBlocking {
            whenever(
                movieRemoteDataSource.searchMoviesByKeyword("Batman", 2)
            ) doThrow Exception()
            searchRepository.searchMoviesByKeyword("Batman", 2)
        }
    }
}
