package leo.me.la.movies

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
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
import leo.me.la.presentation.BaseViewModel


internal class SearchMoviesActivity : AppCompatActivity() {

    private val _viewModel: BaseViewModel<SearchViewState> by viewModel(TAG_SEARCH_VIEWMODEL)
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
        _viewModel.viewStates.observe(this, Observer {
            it?.let { viewState ->
                render(viewState)
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
                loadMovie.visibility = View.VISIBLE
                info.visibility = View.GONE
                moviesList.visibility = View.GONE
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
                moviesList.visibility = View.VISIBLE
                loadMovie.visibility = View.GONE
                movieSection.apply {
                    removeFooter()
                    if (viewState.page == 1)
                        update(emptyList())
                    addAll(viewState.movies.map { MovieItem(it) })
                }
                pagedLoadingHandler.nextPage = if (viewState.page < viewState.totalPages)
                    viewState.page + 1
                else
                    null
            }
            SearchViewState.LoadingNextPage -> {
                movieSection.removeFooter()
                moviesList.post {
                    movieSection.setFooter(LoadingFooter)
                }
            }
            is SearchViewState.LoadPageFailed -> {
                movieSection.removeFooter()
                moviesList.post {
                    movieSection.setFooter(retryLoadNextPageFooter)
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
            setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
            setTextColor(color)
        }
        moviesList.visibility = View.GONE
        loadMovie.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        (searchItem.actionView as SearchView)
            .apply {
                queryHint = "Search Movies"
                setIconifiedByDefault(false)
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let {
                            if (newText.isEmpty())
                                viewModel.resetSearch()
                            else
                                viewModel.searchMovies(it)
                        }
                        return false
                    }
                })
                clearFocus()
            }
        return super.onCreateOptionsMenu(menu)
    }
}
