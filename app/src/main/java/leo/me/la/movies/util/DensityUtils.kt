package leo.me.la.movies.util

import android.content.res.Resources

fun Double.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Float.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Long.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
