package leo.me.la.movies.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import leo.me.la.movies.R

internal object LoadingFooter : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {}

    override fun getLayout() = R.layout.item_loading

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }
}
