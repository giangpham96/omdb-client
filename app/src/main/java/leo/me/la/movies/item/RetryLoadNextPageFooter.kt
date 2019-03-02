package leo.me.la.movies.item

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_retry_load_next_page.retry
import leo.me.la.movies.R

class RetryLoadNextPageFooter(private val onRetryClickListener: () -> Unit) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.retry.setOnClickListener {
            onRetryClickListener()
        }
    }

    override fun getLayout() = R.layout.item_retry_load_next_page

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }
}
