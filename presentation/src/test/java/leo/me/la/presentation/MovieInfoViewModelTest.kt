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
import kotlinx.coroutines.test.TestCoroutineContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieRate
import leo.me.la.common.model.MovieType
import leo.me.la.domain.LoadMovieInfoUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MovieInfoViewModelTest {
    @ObsoleteCoroutinesApi
    private val testCoroutineContext = TestCoroutineContext()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val mainThreadSurrogate = Dispatchers.Unconfined

    private val observer: Observer<MovieInfoViewState> = mockk{
        every { onChanged(any()) } just Runs
    }

    private val useCase: LoadMovieInfoUseCase = mockk()
    private lateinit var viewModel: MovieInfoViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should start in Loading state`() {
        coEvery { useCase.execute("imdbId") } coAnswers {
            delay(500)
            throw Throwable()
        }
        viewModel = MovieInfoViewModel(useCase, testCoroutineContext, "imdbId")
        viewModel.viewStates.observeForever(observer)
        testCoroutineContext.advanceTimeBy(400)
        assertThat(viewModel.viewStates.value).isEqualTo(MovieInfoViewState.Loading)
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to LoadMovieInfoSuccess state`() {
        coEvery { useCase.execute("imdbId") } returns Movie(
            "Spider-Man: Into the Spider-Verse",
            "2018",
            "tt4633694",
            MovieType.Movie,
            "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_SX300.jpg",
            MovieRate.PG,
            "14 Dec 2018",
            "117 min",
            listOf("Animation", "Action", "Adventure", "Family", "Sci-Fi"),
            listOf("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
            listOf("Phil Lord (screenplay by)", "Rodney Rothman (screenplay by)", "Phil Lord (story by)"),
            listOf("Shameik Moore", "Jake Johnson", "Hailee Steinfeld", "Mahershala Ali"),
            "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                    "five counterparts from other dimensions to stop a threat for all realities.",
            listOf("English", "Spanish"),
            listOf("USA", "UK"),
            null,
            87,
            8.6,
            122126,
            null,
            "26 Feb 2019",
            "Sony Pictures",
            "http://www.intothespiderverse.movie/"
        )
        viewModel = MovieInfoViewModel(useCase, imdbId = "imdbId")
        viewModel.viewStates.observeForever(observer)
        assertThat(viewModel.viewStates.value).isEqualTo(
            MovieInfoViewState.LoadMovieInfoSuccess(
                "Spider-Man: Into the Spider-Verse",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_SX300.jpg",
                MovieRate.PG,
                "14 Dec 2018",
                "117 min",
                "Animation, Action, Adventure, Family and Sci-Fi",
                listOf("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
                "Phil Lord (screenplay by), Rodney Rothman (screenplay by) and Phil Lord (story by)",
                listOf("Shameik Moore", "Jake Johnson", "Hailee Steinfeld", "Mahershala Ali"),
                "Teen Miles Morales becomes Spider-Man of his reality, crossing his path with " +
                        "five counterparts from other dimensions to stop a threat for all realities.",
                "English and Spanish",
                "\uD83C\uDDFA\uD83C\uDDF8 \uD83C\uDDEC\uD83C\uDDE7",
                "None",
                "87",
                "8.6",
                "122126",
                "Unknown",
                "26 Feb 2019",
                "Sony Pictures",
                Pair("http://www.intothespiderverse.movie/", true)
            )
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to LoadMovieInfoFailure state`() {
        coEvery { useCase.execute("imdbId") } throws Throwable()
        viewModel = MovieInfoViewModel(useCase, imdbId = "imdbId")
        viewModel.viewStates.observeForever(observer)
        assertThat(viewModel.viewStates.value).isInstanceOf(MovieInfoViewState.LoadMovieInfoFailure::class.java)
    }
}
