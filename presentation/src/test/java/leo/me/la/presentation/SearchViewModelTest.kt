package leo.me.la.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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

    private val observer: Observer<SearchViewState> = mockk {
        every { onChanged(any()) } just Runs
    }

    private val useCase: SearchMoviesUseCase = mockk()
    private lateinit var viewModel: SearchViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        viewModel.viewStates.removeObserver(observer)
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun `should start in Idling state`() {
        assertThat(viewModel.viewStates.value).isEqualTo(SearchViewState(Idle))
    }

    @ObsoleteCoroutinesApi
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
        advanceUntilIdle()
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Batman"))
            observer.onChanged(
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

    @ObsoleteCoroutinesApi
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
        viewModel.searchMovies("Abc")
        advanceTimeBy(500)
        viewModel.searchMovies("Batman")
        advanceTimeBy(1000)

        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(SearchViewState(Loading, "Batman"))
            observer.onChanged(
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

    @ObsoleteCoroutinesApi
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
        viewModel.searchMovies("Abc")
        advanceTimeBy(10)
        viewModel.loadNextPage()
        advanceTimeBy(50)
        viewModel.searchMovies("Batman")
        advanceTimeBy(100)
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(SearchViewState(Loading, "Batman"))
            observer.onChanged(
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

    @ObsoleteCoroutinesApi
    @Test
    fun `should reset to page 1 if new search is dispatched`() {
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
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        viewModel.searchMovies("Batman")
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(SearchViewState(Loading, "Batman"))
            observer.onChanged(
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

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to MovieNotFound state`() = runTest {
        coEvery { useCase.execute(any(), any()) } throws OmdbErrorException("Movie not found!")
        viewModel.searchMovies("Abc")
        advanceUntilIdle()
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
                match {
                    it.data is DataState.Failure && it.data.requireError().message != null
                }
            )
        }
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to SearchFailed state`() {
        with(useCase) {
            coEvery { execute("Abc") } throws OmdbErrorException("empty")
            coEvery { execute("Def") } throws Exception()
        }
        viewModel.searchMovies("Abc")
        viewModel.searchMovies("Def")
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
                match {
                    it.data.failed && it.data.requireError().message == null && it.keyword == "Abc"
                }
            )
            observer.onChanged(SearchViewState(Loading, "Def"))
            observer.onChanged(
                match {
                    it.data.failed && it.data.requireError().message == null && it.keyword == "Def"
                }
            )
        }
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should load next page successfully`() {
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
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(
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

    @ObsoleteCoroutinesApi
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
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(ofType())
        }
        coVerify { useCase.execute(any(), any()) }
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to LoadPageFailed state`() {
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
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        viewModel.loadNextPage()
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(
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

    @ObsoleteCoroutinesApi
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
        viewModel.searchMovies("Abc")
        advanceTimeBy(100)
        viewModel.loadNextPage()
        viewModel.resetSearch()
        advanceTimeBy(1000)
        verifySequence {
            observer.onChanged(SearchViewState(Idle))
            observer.onChanged(SearchViewState(Loading, "Abc"))
            observer.onChanged(
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
            observer.onChanged(
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
            observer.onChanged(SearchViewState(Idle))
        }
    }

    @Test
    fun `should move to MovieNotFound`() {
        coEvery { useCase.execute("Abc") } coAnswers {
            throw OmdbErrorException("Movie not found!")
        }
        viewModel.searchMovies("Abc")
        verify {
            observer.onChanged(
                match {
                    it.data.failed && it.data.requireError().message == "Movie not found!" && it.keyword == "Abc"
                }
            )
        }
    }
}
