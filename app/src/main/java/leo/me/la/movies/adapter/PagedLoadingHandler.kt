package leo.me.la.movies.adapter

internal abstract class PagedLoadingHandler {

    var pageSize: Int = 10

    var isLoadingPage: Boolean = false
        private set

    var nextPage: Int? = null
        set(value) {
            field = value
            isLoadingPage = false
        }

    /**
     * Called when the next page should be loaded. Called from UI thread, so keep it short.
     */
    abstract fun onLoadNextPage()

    val hasNextPage
        get() = nextPage != null

    /**
     * Checks if the handler should load the next page
     *
     * @param position Current position of the adapter
     * @param totalItemCount Total number of items in the adapter
     */
    fun checkForNewPage(position: Int, totalItemCount: Int) {
        //If we are not loading anything, and there are more pages
        if (!isLoadingPage && hasNextPage) {
            //Load new page if the 1st page has been loaded and when there's about half a page items
            // left to display:
            if (totalItemCount > 0 && position >= totalItemCount - pageSize / 2) {
                isLoadingPage = true
                onLoadNextPage()
            }
        }
    }

}
