package leo.me.la.movies

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
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
import kotlinx.android.synthetic.main.fragment_movie_info.loading
import kotlinx.android.synthetic.main.fragment_movie_info.metaScore
import kotlinx.android.synthetic.main.fragment_movie_info.placeholderMetascore
import kotlinx.android.synthetic.main.fragment_movie_info.poster
import kotlinx.android.synthetic.main.fragment_movie_info.runtime
import kotlinx.android.synthetic.main.fragment_movie_info.title
import kotlinx.android.synthetic.main.fragment_movie_info.type
import kotlinx.android.synthetic.main.front_view_movie_info.genres
import kotlinx.android.synthetic.main.front_view_movie_info.plot
import kotlinx.android.synthetic.main.front_view_movie_info.plotContainer
import kotlinx.android.synthetic.main.front_view_movie_info.rate
import leo.me.la.common.TAG_MOVIE_INFO_VIEWMODEL
import leo.me.la.common.model.MovieType
import leo.me.la.movies.item.NameItem
import leo.me.la.movies.util.loadUri
import leo.me.la.presentation.BaseViewModel
import leo.me.la.presentation.DataState
import leo.me.la.presentation.MovieInfoViewModel
import leo.me.la.presentation.MovieInfoViewState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

private const val IMDB_ID = "imdb_id"
private const val POSTER_URL = "poster_url"

internal class MovieInfoFragment : Fragment() {
    private lateinit var imdbId: String

    private val _viewModel: BaseViewModel<MovieInfoViewState>
            by viewModel(named(TAG_MOVIE_INFO_VIEWMODEL)) {
                parametersOf(imdbId)
            }

    private val viewModel by lazy {
        _viewModel as MovieInfoViewModel
    }

    private val directors = Section()
    private val directorsAdapter = GroupAdapter<GroupieViewHolder>()
        .apply {
            spanCount = 3
            add(directors)
        }

    private val actors = Section()
    private val actorsAdapter = GroupAdapter<GroupieViewHolder>()
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
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        poster.loadUri(
            uri = arguments?.getString(POSTER_URL),
            errorImage = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.movie_theater
            )
        )
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
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                render(it)
            }
        }
        info.post {
            val scrollViewHeight = info.measuredHeight
            plotContainer.post {
                plotContainer.minimumHeight = scrollViewHeight
            }
        }
    }

    private fun render(viewState: MovieInfoViewState) {
        when (val state = viewState.state) {
            is DataState.Success -> {
                loading.isVisible = false
                setOf(
                    title, type, imdbRate, imdbVotes, metaScore, runtime, info, placeholderMetascore
                ).forEach {
                    it.isVisible = true
                }
                title.text = state.data.title
                type.apply {
                    when (state.data.type) {
                        MovieType.Movie -> setImageResource(R.drawable.icon_movie)
                        MovieType.Series -> setImageResource(R.drawable.icon_series)
                        else -> isVisible = false
                    }
                }
                imdbRate.text = state.data.imdbRating
                imdbVotes.text = state.data.imdbVotes
                metaScore.text = state.data.metaScore
                runtime.text = state.data.runtime
                rate.text = state.data.rated.name.replace("_", "-")
                plot.text = state.data.plot
                production.text = state.data.production
                released.text = state.data.released
                dvdReleased.text = state.data.dvdRelease
                boxOffice.text = state.data.boxOffice
                writers.text = state.data.writers
                awards.text = state.data.awards
                languages.text = state.data.languages
                countries.text = state.data.countries
                directors.apply {
                    update(state.data.directors.map {
                        NameItem(it)
                    })
                }
                actors.apply {
                    update(state.data.actors.map {
                        NameItem(it)
                    })
                }
                state.data.genres.forEach { genre ->
                    genres.addView(
                        Chip(genres.context).also {
                            it.text = genre
                            it.chipBackgroundColor = ColorStateList.valueOf(Color.GRAY)
                            it.setTextColor(Color.WHITE)
                        }
                    )
                }
            }

            is DataState.Loading -> {
                loading.isVisible = true
                setOf(
                    title, type, imdbRate, imdbVotes, metaScore, runtime, info, placeholderMetascore
                ).forEach {
                    it.isVisible = false
                }
            }

            else -> {}
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(imdbId: String, posterUrl: String?) =
            MovieInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(IMDB_ID, imdbId)
                    posterUrl?.run {
                        putString(POSTER_URL, this)
                    }
                }
            }
    }
}
