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
import leo.me.la.presentation.DataState.Idle
import leo.me.la.presentation.DataState.Loading
import leo.me.la.presentation.DataState.Success
import leo.me.la.presentation.SearchViewState.SearchUi
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
        } returns MovieSearchResult(
            desiredMovieList,
            3
        )
        viewModel.searchMovies("Batman")

        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = desiredMovieList,
                            page = 1,
                            totalPages = 1,
                            nextPageLoading = false,
                            showReloadNextPage = false
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
            MovieSearchResult(cancelledMovieList, 1)
        }
        coEvery {
            useCase.execute("Batman")
        } returns MovieSearchResult(desiredMovieList, 1)
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(SearchViewState(Loading, "Abc"))
            delay(500)
            viewModel.searchMovies("Batman")
            delay(1000)
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = desiredMovieList,
                            page = 1,
                            totalPages = 1,
                            nextPageLoading = false,
                            showReloadNextPage = false
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
            coEvery { execute("Abc") } returns MovieSearchResult(firstMovieList, 200)
            coEvery { execute("Abc", 2) } coAnswers {
                delay(100)
                throw Exception()
            }
            coEvery { execute("Batman") } returns MovieSearchResult(secondMovieList, 1)
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = true,
                            showReloadNextPage = false,
                        )
                    ), "Abc"
                )
            )
            viewModel.searchMovies("Batman")
            delay(100)
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = secondMovieList,
                            page = 1,
                            totalPages = 1,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
            coEvery { execute("Abc") } returns MovieSearchResult(firstMovieList, 200)
            coEvery { execute("Abc", 2) } returns MovieSearchResult(secondMovieList, 200)
            coEvery { execute("Batman") } returns MovieSearchResult(newSearchMovieList, 1)
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = true,
                            showReloadNextPage = false,
                        )
                    ), "Abc"
                )
            )
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList + secondMovieList,
                            page = 2,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
                        )
                    ), "Abc"
                )
            )
            viewModel.searchMovies("Batman")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Batman",
                            movies = newSearchMovieList,
                            page = 1,
                            totalPages = 1,
                            nextPageLoading = false,
                            showReloadNextPage = false,
                        )
                    ), "Batman"
                )
            )
        }
    }

    @Test
    fun `should move to MovieNotFound state`() = runTest {
        coEvery { useCase.execute(any(), any()) } throws OmdbErrorException("Movie not found!")
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).matches {
                it.searchState is DataState.Failure && it.searchState.requireError().message != null
            }
        }
    }

    @Test
    fun `should move to SearchFailed state`() = runTest {
        with(useCase) {
            coEvery { execute("Abc") } throws OmdbErrorException("empty")
            coEvery { execute("Def") } throws Exception()
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).matches {
                it.searchState.failed && it.searchState.requireError().message == null && it.keyword == "Abc"
            }
            viewModel.searchMovies("Def")
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
            coEvery { execute("Abc") } returns MovieSearchResult(firstMovieList, 200)
            coEvery { execute("Abc", 2) } returns MovieSearchResult(secondMovieList, 200)
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = true,
                            showReloadNextPage = false,
                        )
                    ), "Abc"
                )
            )
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList + secondMovieList,
                            page = 2,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
                        )
                    ), "Abc"
                )
            )
        }
    }

    @Test
    fun `shouldn't load next page at all if total result is less than 10`() = runTest {
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
        } returns MovieSearchResult(firstMovieList, 3)
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 1,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
        } returns MovieSearchResult(firstMovieList, 200)
        coEvery {
            useCase.execute("Abc", 2)
        } throws Exception()

        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = true,
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
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = true,
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
            coEvery { execute("Abc") } returns MovieSearchResult(firstMovieList, 200)
            coEvery { execute("Abc", 2) } coAnswers {
                delay(1000)
                MovieSearchResult(secondMovieList, 200)
            }
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).isEqualTo(
                SearchViewState(
                    Success(
                        SearchUi(
                            keyword = "Abc",
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = false,
                            showReloadNextPage = false,
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
                            movies = firstMovieList,
                            page = 1,
                            totalPages = 20,
                            nextPageLoading = true,
                            showReloadNextPage = false,
                        )
                    ), "Abc"
                )
            )
            viewModel.resetSearch()
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
        }
    }

    @Test
    fun `should move to MovieNotFound`() = runTest {
        coEvery { useCase.execute("Abc") } coAnswers {
            throw OmdbErrorException("Movie not found!")
        }
        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(SearchViewState(Idle))
            viewModel.searchMovies("Abc")
            assertThat(awaitItem()).matches {
                it.searchState.failed && it.searchState.requireError().message == "Movie not found!" && it.keyword == "Abc"
            }
        }
    }
}
