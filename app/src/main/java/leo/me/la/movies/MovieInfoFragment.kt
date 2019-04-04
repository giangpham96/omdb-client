package leo.me.la.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.back_view_movie_info.actorsRecyclerview
import kotlinx.android.synthetic.main.back_view_movie_info.awards
import kotlinx.android.synthetic.main.back_view_movie_info.boxOffice
import kotlinx.android.synthetic.main.back_view_movie_info.countries
import kotlinx.android.synthetic.main.back_view_movie_info.directorsRecyclerView
import kotlinx.android.synthetic.main.back_view_movie_info.dvdReleased
import kotlinx.android.synthetic.main.back_view_movie_info.languages
import kotlinx.android.synthetic.main.back_view_movie_info.production
import kotlinx.android.synthetic.main.back_view_movie_info.released
import kotlinx.android.synthetic.main.back_view_movie_info.writers
import kotlinx.android.synthetic.main.fragment_movie_info.imdbRate
import kotlinx.android.synthetic.main.fragment_movie_info.imdbVotes
import kotlinx.android.synthetic.main.fragment_movie_info.info
import kotlinx.android.synthetic.main.fragment_movie_info.metaScore
import kotlinx.android.synthetic.main.fragment_movie_info.poster
import kotlinx.android.synthetic.main.fragment_movie_info.runtime
import kotlinx.android.synthetic.main.fragment_movie_info.title
import kotlinx.android.synthetic.main.fragment_movie_info.type
import kotlinx.android.synthetic.main.front_view_movie_info.plot
import kotlinx.android.synthetic.main.front_view_movie_info.plotContainer
import kotlinx.android.synthetic.main.front_view_movie_info.rate
import leo.me.la.common.TAG_MOVIE_INFO_VIEWMODEL
import leo.me.la.common.model.MovieType
import leo.me.la.movies.item.NameItem
import leo.me.la.presentation.BaseViewModel
import leo.me.la.presentation.MovieInfoViewModel
import leo.me.la.presentation.MovieInfoViewState
import loadUri
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val IMDB_ID = "imdb_id"

internal class MovieInfoFragment : Fragment() {
    private lateinit var imdbId: String

    private val _viewModel: BaseViewModel<MovieInfoViewState>
        by viewModel(name = TAG_MOVIE_INFO_VIEWMODEL) {
            parametersOf(imdbId)
        }

    private val viewModel by lazy {
        _viewModel as MovieInfoViewModel
    }

    private val directors = Section()
    private val directorsAdapter = GroupAdapter<ViewHolder>()
        .apply {
            spanCount = 3
            add(directors)
        }

    private val actors = Section()
    private val actorsAdapter = GroupAdapter<ViewHolder>()
        .apply {
            spanCount = 3
            add(actors)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imdbId = arguments?.getString(IMDB_ID) ?: throw IllegalStateException("imdb_id is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actorsRecyclerview.apply {
            layoutManager = GridLayoutManager(
                this@MovieInfoFragment.requireContext(),
                this@MovieInfoFragment.actorsAdapter.spanCount
            ).apply {
                spanSizeLookup = this@MovieInfoFragment.actorsAdapter.spanSizeLookup
            }
            adapter = this@MovieInfoFragment.actorsAdapter
            ViewCompat.setNestedScrollingEnabled(this, false)
        }
        directorsRecyclerView.apply {
            layoutManager = GridLayoutManager(
                this@MovieInfoFragment.requireContext(),
                this@MovieInfoFragment.directorsAdapter.spanCount
            ).apply {
                spanSizeLookup = this@MovieInfoFragment.directorsAdapter.spanSizeLookup
            }
            adapter = this@MovieInfoFragment.directorsAdapter
            ViewCompat.setNestedScrollingEnabled(this, false)
        }
        viewModel.viewStates.observe(this, Observer {
            it?.let { viewState ->
                render(viewState)
            }
        })
        info.post {
            val scrollViewHeight = info.measuredHeight
            plotContainer.post {
                plotContainer.minimumHeight = scrollViewHeight
            }
        }
    }

    private fun render(viewState: MovieInfoViewState) {
        when (viewState) {
            is MovieInfoViewState.LoadMovieInfoSuccess -> {
                poster.loadUri(
                    viewState.poster,
                    errorImage = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.movie_theater
                    )
                )
                title.text = viewState.title
                type.apply {
                    when (viewState.type) {
                        MovieType.Movie -> setImageResource(R.drawable.icon_movie)
                        MovieType.Series -> setImageResource(R.drawable.icon_series)
                        else -> isVisible = false
                    }
                }
                imdbRate.text = viewState.imdbRating
                imdbVotes.text = viewState.imdbVotes
                metaScore.text = viewState.metaScore
                runtime.text = viewState.runtime
                rate.text = viewState.rated.name.replace("_", "-")
                plot.text = viewState.plot
                production.text = viewState.production
                released.text = viewState.released
                dvdReleased.text = viewState.dvdRelease
                boxOffice.text = viewState.boxOffice
                writers.text = viewState.writers
                awards.text = viewState.awards
                languages.text = viewState.languages
                countries.text = viewState.countries
                directors.apply {
                    update(viewState.directors.map {
                        NameItem(it)
                    })
                }
                actors.apply {
                    update(viewState.actors.map {
                        NameItem(it)
                    })
                }
//                viewState.genres.forEach { genre ->
//                    genres.addView(
//                        Chip(this@MovieInfoFragment.requireContext()).also {
//                            it.text = genre
//                            it.chipBackgroundColor = ColorStateList.valueOf(Color.GRAY)
//                            it.setTextColor(Color.WHITE)
//                        }
//                    )
//                }
            }
            is MovieInfoViewState.LoadMovieInfoFailure -> {

            }
            MovieInfoViewState.Loading -> {

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(imdbId: String) =
            MovieInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(IMDB_ID, imdbId)
                }
            }
    }
}
