package leo.me.la.movies.adapter

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

internal class PaginatedGroupAdapter : GroupAdapter<ViewHolder>() {

    var pagedLoadingHandler: PagedLoadingHandler? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        pagedLoadingHandler?.checkForNewPage(position, itemCount)
    }
}
