package leo.me.la.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.domain.repository.MovieRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SearchMoviesUseCaseImplTest {
    private val repository: MovieRepository = mockk()
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
            coEvery {
                repository.searchMoviesByKeyword("Batman", 2)
            } returns desiredResult
            val actualResult = useCase.execute("Batman", 2)
            assertThat(actualResult).isEqualTo(desiredResult)
            coVerify { repository.searchMoviesByKeyword("Batman", 2) }
        }
    }
    @Test(expected = Exception::class)
    fun `should propagate exception if repository raises one`() {
        runBlocking {
            coEvery {
                repository.searchMoviesByKeyword("Batman", 2)
            } throws Exception()
            useCase.execute("Batman", 2)
        }
    }
}
