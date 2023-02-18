package leo.me.la.movies.item

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.xwray.groupie.viewbinding.BindableItem
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import leo.me.la.movies.R
import leo.me.la.movies.databinding.ItemMovieBinding
import leo.me.la.movies.util.loadUri

internal class MovieItem(
    private val movie: Movie,
    private val onClickListener: (String) -> Unit
) : BindableItem<ItemMovieBinding>() {

    override fun bind(binding: ItemMovieBinding, position: Int) = with (binding) {
        title.text = movie.title
        year.text = movie.year
        type.apply {
            setImageResource(
                when (movie.type) {
                    MovieType.Movie -> R.drawable.icon_movie
                    MovieType.Series -> R.drawable.icon_series
                    else -> R.drawable.icon_series
                }
            )
            visibility = when (movie.type) {
                MovieType.Movie, MovieType.Series -> View.VISIBLE
                else -> View.GONE
            }
        }
        poster.apply {
            loadUri(
                movie.poster,
                errorImage = AppCompatResources.getDrawable(this.context,
                    when (movie.type) {
                        MovieType.Movie -> R.drawable.error_movie_poster
                        MovieType.Series -> R.drawable.error_series_poster
                        else -> R.drawable.error_unknown_poster
                    }),
                onError = {
                    type.visibility = View.GONE
                }
            )
        }
        root.setOnClickListener {
            onClickListener(movie.imdbId)
        }
    }

    override fun getLayout() = R.layout.item_movie

    override fun initializeViewBinding(view: View): ItemMovieBinding {
        return ItemMovieBinding.bind(view)
            .apply {
                type.background = AppCompatResources.getDrawable(
                    view.context,
                    R.drawable.type_background
                )
            }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other is MovieItem) {
            return movie.imdbId == other.movie.imdbId
        }
        return super.isSameAs(other)
    }

    override fun equals(other: Any?): Boolean {
        if (other is MovieItem)
            return movie == other.movie
        return super.equals(other)
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 2
    }

    override fun hashCode(): Int {
        return movie.hashCode()
    }
}
