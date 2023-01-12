package leo.me.la.movies.item

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_movie.poster
import leo.me.la.movies.R
import kotlinx.android.synthetic.main.item_movie.title
import kotlinx.android.synthetic.main.item_movie.type
import kotlinx.android.synthetic.main.item_movie.year
import leo.me.la.common.model.Movie
import leo.me.la.common.model.MovieType
import loadUri

internal class MovieItem(
    private val movie: Movie,
    private val onClickListener: (String) -> Unit
) : Item() {
    override fun createViewHolder(itemView: View): GroupieViewHolder {
        return super.createViewHolder(itemView)
            .apply {
                type.background = AppCompatResources.getDrawable(
                    itemView.context,
                    R.drawable.type_background
                )
            }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.title.text = movie.title
        viewHolder.year.text = movie.year
        viewHolder.type.apply {
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
        viewHolder.poster.apply {
            loadUri(
                movie.poster,
                errorImage = AppCompatResources.getDrawable(this.context,
                    when (movie.type) {
                        MovieType.Movie -> R.drawable.error_movie_poster
                        MovieType.Series -> R.drawable.error_series_poster
                        else -> R.drawable.error_unknown_poster
                    }),
                onError = {
                    viewHolder.type.visibility = View.GONE
                }
            )
        }
        viewHolder.itemView.setOnClickListener {
            onClickListener(movie.imdbId)
        }
    }

    override fun getLayout() = R.layout.item_movie

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
