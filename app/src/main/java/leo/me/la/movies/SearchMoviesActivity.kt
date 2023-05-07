package leo.me.la.movies

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import leo.me.la.movies.search_movies.SearchMoviesScreen
import leo.me.la.presentation.SearchViewModel
import org.koin.androidx.compose.koinViewModel


internal class SearchMoviesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                val viewModel: SearchViewModel = koinViewModel()
                SearchMoviesScreen(
                    viewState = viewModel.viewState.collectAsStateWithLifecycle().value,
                    onQueryChange = viewModel::searchMovies,
                    onLoadingNextPage = viewModel::loadNextPage,
                )
            }
        }
    }
}
