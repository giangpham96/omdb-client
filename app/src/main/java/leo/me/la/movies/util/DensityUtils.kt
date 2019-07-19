package leo.me.la.movies.util

import android.content.res.Resources

fun Float.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
