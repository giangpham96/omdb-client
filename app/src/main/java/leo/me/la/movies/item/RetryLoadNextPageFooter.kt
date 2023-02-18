package leo.me.la.movies.item

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.xwray.groupie.viewbinding.BindableItem
import leo.me.la.movies.R
import leo.me.la.movies.databinding.ItemRetryLoadNextPageBinding

internal class RetryLoadNextPageFooter(
    private val onRetryClickListener: () -> Unit,
    ) : BindableItem<ItemRetryLoadNextPageBinding>() {

    override fun bind(binding: ItemRetryLoadNextPageBinding, position: Int) {
        binding.retry.setOnClickListener {
            onRetryClickListener()
        }
    }

    override fun initializeViewBinding(view: View): ItemRetryLoadNextPageBinding {
        return ItemRetryLoadNextPageBinding.bind(view)
            .apply {
                retry.setOnClickListener {
                    onRetryClickListener()
                }
                retry.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(view.context, R.drawable.icon_retry),
                    null,
                    null
                )
            }
    }

    override fun getLayout() = R.layout.item_retry_load_next_page

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }
}
