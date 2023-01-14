package leo.me.la.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieRate
import leo.me.la.common.model.MovieType
import leo.me.la.domain.LoadMovieInfoUseCase
import leo.me.la.presentation.MovieInfoViewState.LoadMovieInfoFailure
import leo.me.la.presentation.MovieInfoViewState.LoadMovieInfoSuccess
import leo.me.la.presentation.MovieInfoViewState.Loading
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class MovieInfoViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observer: Observer<MovieInfoViewState> = mockk {
        every { onChanged(any()) } just Runs
    }

    private val useCase: LoadMovieInfoUseCase = mockk()
    private lateinit var viewModel: MovieInfoViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        viewModel.viewStates.removeObserver(observer)
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should start in Loading state`() = runTest {
        coEvery { useCase.execute("imdbId") } coAnswers {
            delay(500)
            throw Throwable()
        }
        viewModel = MovieInfoViewModel(useCase, "imdbId", null)
        viewModel.viewStates.observeForever(observer)
        advanceTimeBy(400)
        assertThat(viewModel.viewStates.value).isEqualTo(Loading(null))
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to LoadMovieInfoSuccess state`() = runTest {
        coEvery { useCase.execute("imdbId") } returns Movie(
            title = "Spider-Man: Into the Spider-Verse",
            year = "2018",
            imdbId = "tt4633694",
            type = MovieType.Movie,
            poster = "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNT" +
                    "Q3NjM@._V1_SX300.jpg",
            rated = MovieRate.PG,
            released = "14 Dec 2018",
            runtime = "117 min",
            genres = listOf("Animation", "Action", "Adventure", "Family", "Sci-Fi"),
            directors = listOf("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
            writers = listOf(
                "Phil Lord (screenplay by)",
                "Rodney Rothman (screenplay by)",
                "Phil Lord (story by)"
            ),
            actors = listOf("Shameik Moore", "Jake Johnson", "Hailee Steinfeld", "Mahershala Ali"),
            plot = "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                    "five counterparts from other dimensions to stop a threat for all realities.",
            languages = listOf("English", "Spanish"),
            countries = listOf("USA", "UK"),
            awards = null,
            metaScore = 87,
            imdbRating = 8.6,
            imdbVotes = 122126,
            boxOffice = null,
            dvdRelease = "26 Feb 2019",
            production = "Sony Pictures",
            website = "http://www.intothespiderverse.movie/"
        )
        viewModel = MovieInfoViewModel(useCase, "imdbId", null)
        viewModel.viewStates.observeForever(observer)
        advanceUntilIdle()
        assertThat(viewModel.viewStates.value).isEqualTo(
            LoadMovieInfoSuccess(
                title = "Spider-Man: Into the Spider-Verse",
                type = MovieType.Movie,
                poster = "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNT" +
                        "kwNTQ3NjM@._V1_SX300.jpg",
                rated = MovieRate.PG,
                released = "14 Dec 2018",
                runtime = "117 min",
                genres = listOf("Animation", "Action", "Adventure", "Family", "Sci-Fi"),
                directors = listOf("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
                writers = "Phil Lord (screenplay by), Rodney Rothman (screenplay by) and Phil Lord (story by)",
                actors = listOf("Shameik Moore", "Jake Johnson", "Hailee Steinfeld", "Mahershala Ali"),
                plot = "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                        "five counterparts from other dimensions to stop a threat for all realities.",
                languages = "English and Spanish",
                countries = "\uD83C\uDDFA\uD83C\uDDF8 \uD83C\uDDEC\uD83C\uDDE7",
                awards = "None",
                metaScore = "87/100",
                imdbRating = "8.6/10 IMDb",
                imdbVotes = "122126 votes",
                boxOffice = "Unknown",
                dvdRelease = "26 Feb 2019",
                production = "Sony Pictures",
                website = Pair("http://www.intothespiderverse.movie/", true)
            )
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to LoadMovieInfoFailure state`() = runTest {
        coEvery { useCase.execute("imdbId") } throws Throwable()
        viewModel = MovieInfoViewModel(useCase, "imdbId", null)
        viewModel.viewStates.observeForever(observer)
        advanceUntilIdle()
        assertThat(viewModel.viewStates.value).isInstanceOf(LoadMovieInfoFailure::class.java)
    }
}
