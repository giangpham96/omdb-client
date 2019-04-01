package leo.me.la.movies

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_movie_info.viewPager

const val MOVIE_IDS = "movie_ids"
const val SELECTED_IMDB_ID = "selected_imdb_id"
internal class MovieInfoActivity : AppCompatActivity() {
    companion object {
        /**
         * Launches LogInActivity
         * @param activity [Activity] that is launching the [LogInActivity]
         */
        fun launch(activity: Activity, movieIds: List<String>, selectedMovieId: String) {
            val intent = Intent(activity, MovieInfoActivity::class.java)
                .apply {
                    putExtra(MOVIE_IDS, arrayListOf<String>().apply { addAll(movieIds) })
                    putExtra(SELECTED_IMDB_ID, selectedMovieId)
                }
            ActivityCompat.startActivity(
                activity,
                intent,
                null
            )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val movieIds = intent?.extras?.getStringArrayList(MOVIE_IDS)
            ?: throw IllegalStateException("${javaClass.simpleName} needs a list of movie imdb ids")
        viewPager.apply {
            adapter = ScreenSlidePagerAdapter(
                movieIds,
                supportFragmentManager
            )
            currentItem = intent?.extras?.getString(SELECTED_IMDB_ID)?.let {
                movieIds.indexOf(it).let { index ->
                    if (index >= 0)
                        index
                    else
                        throw IllegalStateException("SELECTED_IMDB_ID is not inside MOVIE_IDS")
                }
            } ?: throw IllegalStateException("${javaClass.simpleName} needs to know selected movie")
        }
    }
    private inner class ScreenSlidePagerAdapter(
        private val movieIds: List<String>,
        fm: FragmentManager
    ) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = movieIds.size

        override fun getItem(position: Int) = MovieInfoFragment.newInstance(movieIds[position])
    }
}
