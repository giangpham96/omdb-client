package leo.me.la.movies.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_movie.poster
import leo.me.la.movies.R
import kotlinx.android.synthetic.main.item_movie.title
import kotlinx.android.synthetic.main.item_movie.year
import leo.me.la.common.model.Movie
import loadUri

object LoadingFooter : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {}

    override fun getLayout() = R.layout.item_loading

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }
}
