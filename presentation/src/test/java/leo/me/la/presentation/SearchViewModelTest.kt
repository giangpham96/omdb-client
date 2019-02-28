package leo.me.la.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.common.model.MovieType
import leo.me.la.domain.SearchMoviesUseCase
import leo.me.la.exception.OmdbErrorException
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SearchViewModelTest {
    @ObsoleteCoroutinesApi
    private val testCoroutineContext = TestCoroutineContext()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val mainThreadSurrogate = Dispatchers.Unconfined

    private val observer: Observer<SearchViewState> = mock()

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun `should start in Idling state`() {
        val viewModel = SearchViewModel(mock())
        viewModel.viewStates.observeForever(observer)
        assertThat(viewModel.viewStates.value).isEqualTo(SearchViewState.Idling)
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should search successfully and move to MoviesFetched state`() {
        val useCase: SearchMoviesUseCase = mock()
        val viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
        val desiredMovieList = List(3) {
            Movie(
                "Batman Begins",
                "2005",
                "tt0372784",
                MovieType.Movie,
                "https://m.media-amazon.com/images/M/MV5BZmUwNGU2ZmItMmRiNC00MjhlLTg5YWUtODMyNzkxODYzMmZlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_SX300.jpg"
            )
        }
        useCase.stub {
            onBlocking {
                execute("Batman")
            } doReturn MovieSearchResult(
                desiredMovieList,
                3
            )
        }
        viewModel.searchMovies("Batman")
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer).onChanged(eq(SearchViewState.Searching))
        verify(observer).onChanged(
            eq(
                SearchViewState.MoviesFetched(
                    "Batman",
                    desiredMovieList,
                    1,
                    1
                )
            )
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should cancel previous search if new search is dispatched`() {
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
        val useCase: SearchMoviesUseCase = object : SearchMoviesUseCase {
            override suspend fun execute(keyword: String, page: Int): MovieSearchResult {
                return if (keyword == "Abc") {
                    delay(1000)
                    MovieSearchResult(cancelledMovieList, 1)
                } else {
                    MovieSearchResult(desiredMovieList, 1)
                }
            }
        }
        val viewModel = SearchViewModel(useCase, testCoroutineContext)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        testCoroutineContext.advanceTimeBy(500)
        viewModel.searchMovies("Batman")
        testCoroutineContext.advanceTimeBy(1000)
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer, times(2)).onChanged(eq(SearchViewState.Searching))
        verify(observer).onChanged(
            eq(
                SearchViewState.MoviesFetched(
                    "Batman",
                    desiredMovieList,
                    1,
                    1
                )
            )
        )
        verify(observer, never()).onChanged(
            eq(
                SearchViewState.MoviesFetched(
                    "Abc",
                    cancelledMovieList,
                    1,
                    1
                )
            )
        )
        verify(observer, never()).onChanged(
            eq(SearchViewState.LoadingNextPage)
        )
        verify(observer, never()).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should cancel next page loading if new search is dispatched`() {
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
        val useCase: SearchMoviesUseCase = object : SearchMoviesUseCase {
            override suspend fun execute(keyword: String, page: Int): MovieSearchResult {
                return if (keyword == "Abc" && page == 1) {
                    MovieSearchResult(firstMovieList, 200)
                } else if (keyword == "Abc" && page == 2) {
                    delay(1000)
                    throw Exception()
                } else {
                    MovieSearchResult(secondMovieList, 1)
                }
            }
        }
        val viewModel = SearchViewModel(useCase, testCoroutineContext)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        testCoroutineContext.advanceTimeBy(100)
        viewModel.loadNextPage()
        testCoroutineContext.advanceTimeBy(500)
        viewModel.searchMovies("Batman")
        testCoroutineContext.advanceTimeBy(1000)
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer).onChanged(eq(SearchViewState.LoadingNextPage))
        verify(observer, times(2)).onChanged(eq(SearchViewState.Searching))
        verify(observer).onChanged(
            eq(
                SearchViewState.MoviesFetched(
                    "Batman",
                    secondMovieList,
                    1,
                    1
                )
            )
        )
        verify(observer).onChanged(
            eq(
                SearchViewState.MoviesFetched(
                    "Abc",
                    firstMovieList,
                    1,
                    20
                )
            )
        )
        verify(observer, never()).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to MovieNotFound state`() {
        val useCase = object : SearchMoviesUseCase {
            override suspend fun execute(keyword: String, page: Int): MovieSearchResult {
                throw OmdbErrorException("Movie not found!")
            }
        }
        val viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer).onChanged(eq(SearchViewState.MovieNotFound))
        verify(observer, never()).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
        verify(observer, never()).onChanged(
            isA<SearchViewState.MoviesFetched>()
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `should move to SearchFailed state`() {
        val useCase = object : SearchMoviesUseCase {
            override suspend fun execute(keyword: String, page: Int): MovieSearchResult {
                if (keyword == "Abc")
                    throw OmdbErrorException("empty")
                else
                    throw Exception()
            }
        }
        val viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        viewModel.searchMovies("Def")
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer, times(2)).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(eq(SearchViewState.MovieNotFound))
        verify(observer, never()).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
        verify(observer, never()).onChanged(
            isA<SearchViewState.MoviesFetched>()
        )
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
        val useCase : SearchMoviesUseCase = mock {
            onBlocking {
                execute("Abc")
            } doReturn MovieSearchResult(firstMovieList, 200)
            onBlocking {
                execute("Abc", 2)
            } doReturn MovieSearchResult(secondMovieList, 200)
        }
        val viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer, never()).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(eq(SearchViewState.MovieNotFound))
        verify(observer, never()).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
        verify(observer, times(2)).onChanged(
            isA<SearchViewState.MoviesFetched>()
        )
    }

    @ObsoleteCoroutinesApi
    @Test
    fun `shouldn't load next page at all if total result is less than 10`() {
        val firstMovieList = List(3) {
            Movie(
                "Abc",
                "2001",
                "tt0372781",
                MovieType.Series,
                ""
            )
        }
        val useCase : SearchMoviesUseCase = mock {
            onBlocking {
                execute("Abc")
            } doReturn MovieSearchResult(firstMovieList, 3)
        }
        val viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer).onChanged(eq(SearchViewState.Searching))
        verify(observer).onChanged(
            isA<SearchViewState.MoviesFetched>()
        )
        verify(observer, never()).onChanged(eq(SearchViewState.LoadingNextPage))
        verify(observer, never()).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(eq(SearchViewState.MovieNotFound))
        verify(observer, never()).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
        runBlocking {
            verify(useCase).execute(any(), any())
        }
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
        val useCase : SearchMoviesUseCase = mock {
            onBlocking {
                execute("Abc")
            } doReturn MovieSearchResult(firstMovieList, 200)
        }
        val viewModel = SearchViewModel(useCase)
        viewModel.viewStates.observeForever(observer)
        viewModel.searchMovies("Abc")
        viewModel.loadNextPage()
        viewModel.loadNextPage()
        verify(observer).onChanged(eq(SearchViewState.Idling))
        verify(observer).onChanged(eq(SearchViewState.Searching))
        verify(observer, times(2)).onChanged(
            isA<SearchViewState.LoadPageFailed>()
        )
        verify(observer).onChanged(
            isA<SearchViewState.MoviesFetched>()
        )
        verify(observer, times(2)).onChanged(eq(SearchViewState.LoadingNextPage))
        verify(observer, never()).onChanged(
            eq(SearchViewState.SearchFailed)
        )
        verify(observer, never()).onChanged(eq(SearchViewState.MovieNotFound))
        runBlocking {
            verify(useCase, times(2)).execute("Abc", 2)
        }
    }
}
