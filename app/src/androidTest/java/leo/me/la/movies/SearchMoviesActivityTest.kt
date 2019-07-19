package leo.me.la.movies

import androidx.core.content.ContextCompat.getColor
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import leo.me.la.common.TAG_SEARCH_VIEWMODEL
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import leo.me.la.presentation.BaseViewModel
import leo.me.la.presentation.SearchViewState
import leo.me.la.presentation.baseViewModel
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

@LargeTest
internal class SearchMoviesActivityTest : KoinTest {
    @get:Rule
    val activityTestRule = ActivityTestRule<SearchMoviesActivity>(SearchMoviesActivity::class.java, false, false)

    private val viewModel = MockedViewModel

    @Before
    fun setup() {
        loadKoinModules(module(override = true) {
            baseViewModel(TAG_SEARCH_VIEWMODEL) { viewModel }
        })
        activityTestRule.launchActivity(null)
    }

    @Test
    fun shouldRenderIdlingState() {
        viewModel.moveToState(SearchViewState.Idling)
        R.id.loadMovie.checkInvisible()
        R.id.toolbar.checkVisible()
        R.id.info.run {
            checkVisible()
            withText("Search movie")
            withTextColor(
                getColor(
                    activityTestRule.activity,
                    R.color.colorPrimary
                )
            )
            withDrawables(0, R.drawable.cinema, 0, 0)
        }
    }

    @Test
    fun shouldRenderSearchingState() {
        viewModel.moveToState(SearchViewState.Searching)
        R.id.info.checkInvisible()
        R.id.loadMovie.checkVisible()
        R.id.toolbar.checkVisible()
    }

    @Test
    fun shouldRenderMoviesFetchedState() {
        viewModel.moveToState(
            SearchViewState.MoviesFetched(
                "keyword",
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
                        MovieType.Series,
                        null
                    ),
                    Movie(
                        "Batman: The Dark Knight Returns, Part 2",
                        "2013",
                        "tt2313198",
                        MovieType.Other,
                        "https://m.media-amazon.com/images/M/MV5BOTM3MTRkZjQtYjBkMy00YWE1LTkxOTQtNDQyNGY0YjYzNzAzXkEyXkFqcGdeQXVyOTgwMzk1MTA@._V1_SX300.jpg"
                    )
                ),
                1,
                1
            )
        )
        R.id.moviesList.run {
            checkVisible()
            checkItemAtPosition<ViewHolder>(
                0,
                hasDescendant(allOf(withId(R.id.title), ViewMatchers.withText("Batman Begins"))),
                hasDescendant(allOf(withId(R.id.year), ViewMatchers.withText("2005"))),
                hasDescendant(allOf(withId(R.id.type), withDrawable(R.drawable.icon_movie), isDisplayed()))
            )
            checkItemAtPosition<ViewHolder>(
                1,
                hasDescendant(allOf(withId(R.id.title), ViewMatchers.withText("Batman: The Animated Series"))),
                hasDescendant(allOf(withId(R.id.year), ViewMatchers.withText("1992–1995"))),
                hasDescendant(allOf(withId(R.id.type), withDrawable(R.drawable.icon_series), isDisplayed()))
            )
            checkItemAtPosition<ViewHolder>(
                2,
                hasDescendant(allOf(withId(R.id.title), ViewMatchers.withText("Batman: The Dark Knight Returns, Part 1"))),
                hasDescendant(allOf(withId(R.id.year), ViewMatchers.withText("2012"))),
                hasDescendant(allOf(withId(R.id.type), not(isDisplayed())))
            )
            checkItemAtPosition<ViewHolder>(
                3,
                hasDescendant(allOf(withId(R.id.title), ViewMatchers.withText("Batman: The Dark Knight Returns, Part 2"))),
                hasDescendant(allOf(withId(R.id.year), ViewMatchers.withText("2013"))),
                hasDescendant(allOf(withId(R.id.type), not(isDisplayed())))
            )
        }
        R.id.info.checkInvisible()
        R.id.loadMovie.checkInvisible()
        R.id.toolbar.checkVisible()
    }

    @Test
    fun shouldRenderLoadingNextPageState() {
        viewModel.moveToState(SearchViewState.LoadingNextPage(
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
                    MovieType.Series,
                    null
                ),
                Movie(
                    "Batman: The Dark Knight Returns, Part 2",
                    "2013",
                    "tt2313198",
                    MovieType.Other,
                    "https://m.media-amazon.com/images/M/MV5BOTM3MTRkZjQtYjBkMy00YWE1LTkxOTQtNDQyNGY0YjYzNzAzXkEyXkFqcGdeQXVyOTgwMzk1MTA@._V1_SX300.jpg"
                )
            )
        ))
        R.id.moviesList.checkItemAtPosition<ViewHolder>(
            4,
            hasDescendant(allOf(withId(R.id.loadMore), isDisplayed()))
        )
    }

    @Test
    fun shouldRenderLoadPageFailedState() {
        viewModel.moveToState(SearchViewState.LoadPageFailed(
            "Batman",
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
                    MovieType.Series,
                    null
                ),
                Movie(
                    "Batman: The Dark Knight Returns, Part 2",
                    "2013",
                    "tt2313198",
                    MovieType.Other,
                    "https://m.media-amazon.com/images/M/MV5BOTM3MTRkZjQtYjBkMy00YWE1LTkxOTQtNDQyNGY0YjYzNzAzXkEyXkFqcGdeQXVyOTgwMzk1MTA@._V1_SX300.jpg"
                )
            ),
            2,
            10
        ))
        R.id.moviesList.checkItemAtPosition<ViewHolder>(
            4,
            hasDescendant(allOf(withId(R.id.retry), isDisplayed()))
        )
    }

    @Test
    fun shouldRenderSearchFailedState() {
        viewModel.moveToState(SearchViewState.SearchFailed("Batman"))
        R.id.loadMovie.checkInvisible()
        R.id.toolbar.checkVisible()
        R.id.info.run {
            checkVisible()
            withText("")
            withTextColor(
                getColor(
                    activityTestRule.activity,
                    android.R.color.holo_red_dark
                )
            )
            withDrawables(0, R.drawable.unknown, 0, 0)
        }
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(allOf(withText("Something wrong happens"), isDisplayed())))
    }

    @Test
    fun shouldRenderMovieNotFoundState() {
        viewModel.moveToState(SearchViewState.MovieNotFound)
        R.id.loadMovie.checkInvisible()
        R.id.toolbar.checkVisible()
        R.id.info.run {
            checkVisible()
            withText("Not found")
            withTextColor(
                getColor(
                    activityTestRule.activity,
                    android.R.color.holo_red_dark
                )
            )
            withDrawables(0, R.drawable.not_found, 0, 0)
        }
    }
}

object MockedViewModel : BaseViewModel<SearchViewState>() {
    fun moveToState(state: SearchViewState) {
        _viewStates.postValue(state)
    }
}
