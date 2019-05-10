package leo.me.la.movies

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.activity_search_movies.info
import kotlinx.android.synthetic.main.activity_search_movies.loadMovie
import kotlinx.android.synthetic.main.activity_search_movies.moviesList
import kotlinx.android.synthetic.main.activity_search_movies.toolbar
import leo.me.la.movies.adapter.PagedLoadingHandler
import leo.me.la.movies.adapter.PaginatedGroupAdapter
import leo.me.la.movies.item.LoadingFooter
import leo.me.la.movies.item.MovieItem
import leo.me.la.presentation.SearchViewModel
import leo.me.la.presentation.SearchViewState
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search_movies.root
import leo.me.la.common.TAG_SEARCH_VIEWMODEL
import leo.me.la.movies.item.RetryLoadNextPageFooter
import leo.me.la.movies.util.DebouncingQueryTextListener
import leo.me.la.presentation.BaseViewModel
import org.koin.core.qualifier.named


internal class SearchMoviesActivity : AppCompatActivity() {

    private val _viewModel: BaseViewModel<SearchViewState> by viewModel(named(TAG_SEARCH_VIEWMODEL))
    private val viewModel by lazy {
        _viewModel as SearchViewModel
    }

    private val movieSection = Section()
    private val pagedLoadingHandler = object : PagedLoadingHandler() {
        override fun onLoadNextPage() {
            viewModel.loadNextPage()
        }
    }

    private val adapter = PaginatedGroupAdapter()
        .apply {
            spanCount = 2
            pagedLoadingHandler = this@SearchMoviesActivity.pagedLoadingHandler
            add(movieSection)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movies)
        viewModel.viewStates.observe(this, Observer {
            it?.let { viewState ->
                render(viewState)
            }
        })
        viewModel.navigationRequest.observe(this, Observer { event ->
            event?.let {
                MovieInfoActivity.launch(
                    this@SearchMoviesActivity,
                    it.movies.map { ParcelableMovie(it.imdbId, it.poster)},
                    it.selectedMovie
                )
            }
        })
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        moviesList.apply {
            layoutManager = GridLayoutManager(
                this@SearchMoviesActivity,
                this@SearchMoviesActivity.adapter.spanCount
            ).apply {
                spanSizeLookup = this@SearchMoviesActivity.adapter.spanSizeLookup
            }
            adapter = this@SearchMoviesActivity.adapter
        }
    }

    private var snackBar: Snackbar? = null
    private val retryLoadNextPageFooter = RetryLoadNextPageFooter {
        viewModel.loadNextPage()
    }

    @SuppressLint("SetTextI18n")
    private fun render(viewState: SearchViewState) {
        snackBar?.dismiss()
        when (viewState) {
            SearchViewState.Idling -> {
                showInfo(
                    "Search movie",
                    ContextCompat.getColor(
                        this@SearchMoviesActivity,
                        R.color.colorPrimary
                    ),
                    R.drawable.cinema
                )
            }
            SearchViewState.MovieNotFound -> {
                showInfo(
                    "Not found",
                    ContextCompat.getColor(
                        this@SearchMoviesActivity,
                        android.R.color.holo_red_dark
                    ),
                    R.drawable.not_found
                )
            }
            SearchViewState.Searching -> {
                movieSection.apply {
                    update(emptyList())
                    removeFooter()
                }
                loadMovie.visibility = View.VISIBLE
                info.visibility = View.GONE
            }
            is SearchViewState.SearchFailed -> {
                showInfo(
                    "",
                    ContextCompat.getColor(
                        this@SearchMoviesActivity,
                        android.R.color.holo_red_dark
                    ),
                    R.drawable.unknown
                )
                snackBar = Snackbar.make(root, "Something wrong happens", Snackbar.LENGTH_INDEFINITE)
                    .apply {
                        setAction("Retry") {
                            viewModel.searchMovies(viewState.keyword)
                            dismiss()
                        }
                        show()
                    }
            }
            is SearchViewState.MoviesFetched -> {
                info.visibility = View.GONE
                loadMovie.visibility = View.GONE
                movieSection.apply {
                    removeFooter()
                    update(viewState.movies.map {
                        MovieItem(it) {
                            viewModel.onItemClick(it)
                        }
                    })
                }
                pagedLoadingHandler.nextPage = if (viewState.page < viewState.totalPages)
                    viewState.page + 1
                else
                    null
            }
            is SearchViewState.LoadingNextPage -> {
                moviesList.post {
                    movieSection.apply {
                        removeFooter()
                        update(viewState.movies.map {
                            MovieItem(it) {
                                viewModel.onItemClick(it)
                            }
                        })
                        setFooter(LoadingFooter)
                    }
                }
            }
            is SearchViewState.LoadPageFailed -> {
                moviesList.post {
                    movieSection.apply {
                        removeFooter()
                        setFooter(retryLoadNextPageFooter)
                        update(viewState.movies.map {
                            MovieItem(it) {
                                viewModel.onItemClick(it)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun showInfo(content: String, @ColorInt color: Int, @DrawableRes icon: Int) {
        movieSection.apply {
            update(emptyList())
            removeFooter()
        }
        info.apply {
            visibility = View.VISIBLE
            text = content
            setCompoundDrawablesWithIntrinsicBounds(
                null,
                AppCompatResources.getDrawable(this@SearchMoviesActivity, icon),
                null,
                null
            )
            setTextColor(color)
        }
        loadMovie.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        (searchItem.actionView as SearchView)
            .apply {
                queryHint = "Search Movies"
                setIconifiedByDefault(false)
                setOnQueryTextListener(
                    DebouncingQueryTextListener(
                        this@SearchMoviesActivity.lifecycle
                    ) { newText ->
                        newText?.let {
                            if (it.isEmpty()) {
                                viewModel.resetSearch()
                            } else {
                                viewModel.searchMovies(it)
                            }
                        }
                    }
                )
                clearFocus()
            }
        return super.onCreateOptionsMenu(menu)
    }
}
