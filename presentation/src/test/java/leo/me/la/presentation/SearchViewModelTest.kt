package leo.me.la.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.domain.SearchMoviesUseCase
import leo.me.la.exception.OmdbErrorException
import leo.me.la.presentation.DataState.Companion.failed
import leo.me.la.presentation.DataState.Companion.loading
import leo.me.la.presentation.DataState.Idle
import leo.me.la.presentation.DataState.Loading
import leo.me.la.presentation.DataState.Success
import leo.me.la.presentation.SearchViewState.SearchUi
import leo.me.la.presentation.SearchViewState.SearchUi.LoadingNextPage
import leo.me.la.presentation.SearchViewState.SearchUi.ReloadNextPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val useCase: SearchMoviesUseCase = mockk()
    private lateinit var viewModel: SearchViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(useCase)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun `should start in Idling state`() = runTest {
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
        }
    }

    @Test
    fun `should search successfully and move to MoviesFetched state`() = runTest {
        val desiredMovieList = List(3) {
            Movie(
                "Batman Begins",
                "2005",
                "tt0372784",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
            )
        }

        coEvery {
            useCase.execute("Batman")
        } returns Result.success(
            MovieSearchResult(
                desiredMovieList,
                3
            )
        )
        viewModel.searchMovies("Batman")

        viewModel.viewState.test {
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = desiredMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 1,
                            footer = null,
                        )
                    ), "Batman"
                )
            )
        }
    }

    @Test
    fun `should cancel previous search if new search is dispatched`() = runTest {
        val cancelledMovieList = listOf(
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        )
        val desiredMovieList = listOf(
            Movie(
                "Batman Begins",
                "2005",
                "tt0372784",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
            )
        )
        coEvery {
            useCase.execute("Abc")
        } coAnswers {
            delay(1000)
            Result.success(MovieSearchResult(cancelledMovieList, 1))
        }
        coEvery {
            useCase.execute("Batman")
        } returns Result.success(MovieSearchResult(desiredMovieList, 1))
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            delay(500)
            assertThat(expectMostRecentItem()).isEqualTo(SearchViewState(Loading, "Abc"))
            viewModel.searchMovies("Batman")
            delay(1000)
            assertThat(expectMostRecentItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = listOf(
                                SearchUi.Movie(
                                    title = "Batman Begins",
                                    poster = "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg",
                                    imdbId = "tt0372784"
                                )
                            ),
                            page = 1,
                            totalPages = 1,
                            footer = null,
                        )
                    ), "Batman"
                )
            )
        }
    }

    @Test
    fun `should cancel next page loading if new search is dispatched`() = runTest {
        val firstMovieList = List(10) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        val secondMovieList = listOf(
            Movie(
                "Batman Begins",
                "2005",
                "tt0372784",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
            )
        )
        with(useCase) {
            coEvery { execute("Abc") } returns Result.success(
                MovieSearchResult(
                    firstMovieList,
                    200
                )
            )
            coEvery { execute("Abc", 2) } coAnswers {
                delay(100)
                Result.failure(Exception())
            }
            coEvery { execute("Batman") } returns Result.success(
                MovieSearchResult(
                    secondMovieList,
                    1
                )
            )
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = null,
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            delay(50)
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = LoadingNextPage,
                        )
                    ), "Abc"
                )
            )
            viewModel.searchMovies("Batman")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = secondMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 1,
                            footer = null,
                        )
                    ), "Batman"
                )
            )
        }
    }

    @Test
    fun `should reset to page 1 if new search is dispatched`() = runTest {
        val firstMovieList = List(10) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        val secondMovieList = List(10) {
            Movie(
                "Def",
                "2005",
                "tt0372789",
                MovieType.Movie,
                ""
            )
        }
        val newSearchMovieList = listOf(
            Movie(
                "Batman Begins",
                "2005",
                "tt0372784",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
            )
        )
        with(useCase) {
            coEvery { execute("Abc") } returns Result.success(
                MovieSearchResult(
                    firstMovieList,
                    200
                )
            )
            coEvery { execute("Abc", 2) } returns Result.success(
                MovieSearchResult(
                    secondMovieList,
                    200
                )
            )
            coEvery { execute("Batman") } returns Result.success(
                MovieSearchResult(
                    newSearchMovieList,
                    1
                )
            )
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = null
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = LoadingNextPage
                        )
                    ), "Abc"
                )
            )
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = (firstMovieList + secondMovieList).map(::toUiModel),
                            page = 2,
                            totalPages = 20,
                            footer = null
                        )
                    ), "Abc"
                )
            )
            viewModel.searchMovies("Batman")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = newSearchMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 1,
                            footer = null
                        )
                    ), "Batman"
                )
            )
        }
    }

    @Test
    fun `should move to MovieNotFound state`() = runTest {
        coEvery {
            useCase.execute(
                any(),
                any()
            )
        } returns Result.failure(OmdbErrorException("Movie not found!"))
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    searchState = DataState.Failure(
                        MovieNotFoundException
                    ), keyword = "Abc"
                )
            )
        }
    }

    @Test
    fun `should move to SearchFailed state`() = runTest {
        with(useCase) {
            coEvery { execute("Abc") } returns Result.failure(OmdbErrorException("empty"))
            coEvery { execute("Def") } returns Result.failure(Exception())
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).matches { it.searchState.loading && it.keyword == "Abc" }
            assertThat(awaitItem()).matches {
                it.searchState.failed && it.searchState.requireError().message == null && it.keyword == "Abc"
            }
            viewModel.searchMovies("Def")
            assertThat(awaitItem()).matches { it.searchState.loading && it.keyword == "Def" }
            assertThat(awaitItem()).matches {
                it.searchState.failed && it.searchState.requireError().message == null && it.keyword == "Def"
            }
        }
    }

    @Test
    fun `should load next page successfully`() = runTest {
        val firstMovieList = List(10) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        val secondMovieList = List(10) {
            Movie(
                "Def",
                "2001",
                "tt037278e",
                MovieType.Series,
                ""
            )
        }
        with(useCase) {
            coEvery { execute("Abc") } returns Result.success(
                MovieSearchResult(
                    firstMovieList,
                    200
                )
            )
            coEvery { execute("Abc", 2) } returns Result.success(
                MovieSearchResult(
                    secondMovieList,
                    200
                )
            )
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = null,
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = LoadingNextPage,
                        )
                    ), "Abc"
                )
            )
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = (firstMovieList + secondMovieList).map(::toUiModel),
                            page = 2,
                            totalPages = 20,
                            footer = null,
                        )
                    ), "Abc"
                )
            )
        }
    }

    @Test
    fun `shouldn't load next page at all if total page is 1`() = runTest {
        val firstMovieList = List(3) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        coEvery {
            useCase.execute("Abc")
        } returns Result.success(MovieSearchResult(firstMovieList, 3))
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 1,
                            footer = null
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            delay(1000)
            ensureAllEventsConsumed()
        }
        coVerify(exactly = 1) { useCase.execute(any(), any()) }
    }

    @Test
    fun `should move to LoadPageFailed state`() = runTest {
        val firstMovieList = List(10) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        coEvery {
            useCase.execute("Abc")
        } returns Result.success(MovieSearchResult(firstMovieList, 200))
        coEvery {
            useCase.execute("Abc", 2)
        } returns Result.failure(Exception())

        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = null,
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = ReloadNextPage,
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = ReloadNextPage,
                        )
                    ), "Abc"
                )
            )
        }
        coVerify(exactly = 2) {
            useCase.execute("Abc", 2)
        }
    }

    @Test
    fun `should not allow to load next page if the search is reset`() = runTest {
        val firstMovieList = List(10) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        val secondMovieList = List(10) {
            Movie(
                "Def",
                "2001",
                "tt037278e",
                MovieType.Series,
                ""
            )
        }
        with(useCase) {
            coEvery { execute("Abc") } returns Result.success(
                MovieSearchResult(
                    firstMovieList,
                    200
                )
            )
            coEvery { execute("Abc", 2) } coAnswers {
                delay(1000)
                Result.success(MovieSearchResult(secondMovieList, 200))
            }
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            awaitItem()
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = null,
                        )
                    ), "Abc"
                )
            )
            viewModel.loadNextPage()
            assertThat(expectMostRecentItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList.map(::toUiModel),
                            page = 1,
                            totalPages = 20,
                            footer = LoadingNextPage,
                        )
                    ), "Abc"
                )
            )
            viewModel.searchMovies("")
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
        }
    }

    private fun toUiModel(movie: Movie) =
        movie.let { SearchUi.Movie(it.title, it.poster, it.imdbId) }

}
