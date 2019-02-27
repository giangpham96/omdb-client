package leo.me.la.domain

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.domain.repository.SearchRepository
import org.assertj.core.api.Assertions
import org.junit.Test

class SearchMoviesUseCaseTest {
    private val repository: SearchRepository = mock()
    private val useCase = SearchMoviesUseCaseImpl(repository)


    @Test
    fun `should get correct movies which remote repository provides`() {
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
                repository.searchMoviesByKeyword("Batman", 2)
            ) doReturn desiredResult
            val actualResult = useCase.execute("Batman", 2)
            Assertions.assertThat(actualResult).isEqualTo(desiredResult)
            verify(repository).searchMoviesByKeyword("Batman", 2)
        }
    }
    @Test(expected = Exception::class)
    fun `should propagate exception if repository raises one`() {
        runBlocking {
            whenever(
                repository.searchMoviesByKeyword("Batman", 2)
            ) doThrow Exception()
            useCase.execute("Batman", 2)
        }
    }
}
