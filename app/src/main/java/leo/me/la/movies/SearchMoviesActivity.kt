package leo.me.la.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.activity_search_movies.moviesList
import kotlinx.android.synthetic.main.activity_search_movies.toolbar
import leo.me.la.movies.adapter.PagedLoadingHandler
import leo.me.la.movies.adapter.PaginatedGroupAdapter
import leo.me.la.movies.item.LoadingFooter
import leo.me.la.movies.item.MovieItem
import leo.me.la.presentation.SearchViewModel
import leo.me.la.presentation.SearchViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchMoviesActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()

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
        setSupportActionBar(toolbar)

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

    private fun render(viewState: SearchViewState) {
        when (viewState) {
            SearchViewState.Idling -> {
                movieSection.apply {
                    update(emptyList())
                    removeFooter()
                }
            }
            is SearchViewState.MoviesFetched -> {
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
                moviesList.post {
                    movieSection.setFooter(LoadingFooter)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        (searchItem.actionView as SearchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        return super.onCreateOptionsMenu(menu)
    }
}
