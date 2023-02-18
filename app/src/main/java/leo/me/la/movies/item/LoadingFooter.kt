package leo.me.la.movies.item

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import leo.me.la.movies.R
import leo.me.la.movies.databinding.ItemLoadingBinding

internal object LoadingFooter : BindableItem<ItemLoadingBinding>() {
    override fun bind(viewHolder: ItemLoadingBinding, position: Int) {}

    override fun getLayout() = R.layout.item_loading

    override fun initializeViewBinding(view: View): ItemLoadingBinding {
        return ItemLoadingBinding.bind(view)
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }
}
