package leo.me.la.movies.item

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_retry_load_next_page.retry
import leo.me.la.movies.R

internal class RetryLoadNextPageFooter(private val onRetryClickListener: () -> Unit) : Item() {
    override fun createViewHolder(itemView: View): GroupieViewHolder {
        return super.createViewHolder(itemView)
            .apply {
                retry.setOnClickListener {
                    onRetryClickListener()
                }
                retry.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(itemView.context, R.drawable.icon_retry),
                    null,
                    null
                )
            }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.retry.setOnClickListener {
            onRetryClickListener()
        }
    }

    override fun getLayout() = R.layout.item_retry_load_next_page

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }
}
