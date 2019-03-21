package leo.me.la.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import leo.me.la.domain.repository.MovieRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LoadMovieInfoUseCaseImplTest {
    private val repository: MovieRepository = mockk()
    private val useCase = LoadMovieInfoUseCaseImpl(repository)


    @Test
    fun `should get correct movies which remote repository provides`() {
        val desiredResult = Movie(
            "Spider-Man: Into the Spider-Verse",
            "2018",
            "tt4633694",
            MovieType.Movie,
            "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_SX300.jpg",
            "PG",
            "14 Dec 2018",
            "117 min",
            listOf("Animation", "Action", "Adventure", "Family", "Sci-Fi"),
            listOf("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
            listOf("Phil Lord (screenplay by)", "Rodney Rothman (screenplay by)", "Phil Lord (story by)"),
            listOf("Shameik Moore", "Jake Johnson", "Hailee Steinfeld", "Mahershala Ali"),
            "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                    "five counterparts from other dimensions to stop a threat for all realities.",
            listOf("English", "Spanish"),
            listOf("USA"),
            null,
            87,
            8.6,
            122126,
            null,
            "26 Feb 2019",
            "Sony Pictures",
            "http://www.intothespiderverse.movie/"
        )
        runBlocking {
            coEvery {
                repository.searchMovieByImdbId("tt4633694")
            } returns desiredResult
            val actualResult = useCase.execute("tt4633694")
            assertThat(actualResult).isEqualTo(desiredResult)
            coVerify { repository.searchMovieByImdbId("tt4633694") }
        }
    }

    @Test(expected = Exception::class)
    fun `should propagate exception if repository raises one`() {
        runBlocking {
            coEvery {
                repository.searchMovieByImdbId("tt4633694")
            } throws Exception()
            useCase.execute("tt4633694")
        }
    }
}
