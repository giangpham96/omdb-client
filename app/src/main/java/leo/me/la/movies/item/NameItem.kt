package leo.me.la.movies.item

import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.xwray.groupie.viewbinding.BindableItem
import leo.me.la.movies.R
import leo.me.la.movies.databinding.ItemNameBinding

internal class NameItem(private val name: String) : BindableItem<ItemNameBinding>() {
    override fun bind(binding: ItemNameBinding, position: Int) {
        binding.name.text = HtmlCompat.fromHtml(
            "<a href=\"https://en.wikipedia.org/wiki/${name.replace(" ", "_")}\">$name</a>",
            FROM_HTML_MODE_LEGACY
        )
    }

    override fun initializeViewBinding(view: View): ItemNameBinding {
        return ItemNameBinding.bind(view).apply {
            name.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun getLayout(): Int = R.layout.item_name

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 3
    }
}
