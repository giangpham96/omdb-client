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

const val MOVIES = "movies"
const val SELECTED_IMDB_ID = "selected_imdb_id"

internal class MovieInfoActivity : AppCompatActivity() {
    companion object {
        fun launch(
            activity: Activity, movies: List<ParcelableMovie>, selectedMovieId:
            String
        ) {
            val intent = Intent(activity, MovieInfoActivity::class.java)
                .apply {
                    putExtras(
                        Bundle().apply {
                            putParcelableArrayList(
                                MOVIES,
                                arrayListOf<ParcelableMovie>().apply { addAll(movies) }
                            )
                            putString(SELECTED_IMDB_ID, selectedMovieId)
                        }
                    )
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

        val movieIds =
            intent?.extras?.getParcelableArrayList<ParcelableMovie>(MOVIES)
                ?: throw IllegalStateException("${javaClass.simpleName} needs a map of movie image and imdb ids")
        viewPager.apply {
            val movieList = movieIds.toList()
            adapter = ScreenSlidePagerAdapter(
                movieList,
                supportFragmentManager
            )
            currentItem = intent?.extras?.getString(SELECTED_IMDB_ID)?.let { id ->
                movieList.map { it.imdbId }
                    .indexOf(id)
                    .let { index ->
                        if (index >= 0)
                            index
                        else
                            throw IllegalStateException("SELECTED_IMDB_ID is not inside MOVIES")
                    }
            } ?: throw IllegalStateException("${javaClass.simpleName} needs to know selected movie")
        }
    }
    private inner class ScreenSlidePagerAdapter(
        private val movieImageIds: List<ParcelableMovie>,
        fm: FragmentManager
    ) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = movieImageIds.size

        override fun getItem(position: Int) = MovieInfoFragment
            .newInstance(movieImageIds[position].imdbId, movieImageIds[position].posterUrl)
    }
}
