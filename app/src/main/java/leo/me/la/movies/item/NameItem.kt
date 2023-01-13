package leo.me.la.movies.item

import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_name.name
import leo.me.la.movies.R

internal class NameItem(private val name: String) : Item() {
    override fun createViewHolder(itemView: View): GroupieViewHolder {
        return super.createViewHolder(itemView).apply {
            name.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.name.text = HtmlCompat.fromHtml(
            "<a href=\"https://en.wikipedia.org/wiki/${name.replace(" ", "_")}\">$name</a>",
            FROM_HTML_MODE_LEGACY
        )
    }

    override fun getLayout(): Int = R.layout.item_name

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 3
    }
}
