package leo.me.la.movies

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher

fun Int.checkInvisible() {
    onView(withId(this)).check(matches(not(isDisplayed())))
}

fun Int.checkVisible() {
    onView(withId(this)).check(matches(isDisplayed()))
}

fun Int.withText(text: String) {
    onView(withId(this)).check(matches(ViewMatchers.withText(text)))
}

fun Int.withTextColor(@ColorInt color: Int) {
    onView(withId(this)).check(matches(
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("view with text color same as $color")
            }

            override fun matchesSafely(view: View): Boolean {
                return view is TextView && view.currentTextColor == color
            }
        }
    ))
}

fun Int.withDrawables(
    @DrawableRes leftId: Int,
    @DrawableRes topId: Int,
    @DrawableRes rightId: Int,
    @DrawableRes bottomId: Int
) {
    onView(withId(this)).check(matches(object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("button has drawables")
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is TextView) return false
            val matchLeft = matchDrawable(view.context, view.compoundDrawables[0], leftId)
            val matchTop = matchDrawable(view.context, view.compoundDrawables[1], topId)
            val matchRight = matchDrawable(view.context, view.compoundDrawables[2], rightId)
            val matchBottom = matchDrawable(view.context, view.compoundDrawables[3], bottomId)

            return matchLeft && matchRight && matchTop && matchBottom
        }

        private fun matchDrawable(
            context: Context,
            drawable: Drawable?,
            @DrawableRes drawableId: Int
        ): Boolean {
            // either null and id = 0 or match each other constant state
            return (drawable == null && drawableId == 0)
                    || try {
                drawable?.constantState == ContextCompat.getDrawable(context, drawableId)!!.constantState
            } catch (e: Resources.NotFoundException) {
                val desiredDrawable = VectorDrawableCompat.create(context.resources, drawableId, null)
                desiredDrawable?.toBitmap()?.sameAs(drawable?.toBitmap()) == true
            }
        }
    }))
}

fun <VH : RecyclerView.ViewHolder> Int.checkItemAtPosition(
    position: Int,
    @NonNull vararg itemMatchers: Matcher<View>
) {
    fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View>): BoundedMatcher<View, RecyclerView> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: return false // has no item on such position
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    itemMatchers.forEach { itemMatcher ->
        onView(withId(this))
            .perform(RecyclerViewActions.scrollToPosition<VH>(position))
            .check(matches(atPosition(position, itemMatcher)))
    }
}

fun withDrawable(@DrawableRes id: Int, @ColorInt tint: Int? = null, tintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN): TypeSafeMatcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("ImageView with drawable same as drawable with id $id")
            tint?.let { description.appendText(", tint color id: $tint, mode: $tintMode") }
        }

        override fun matchesSafely(view: View): Boolean {
            val context = view.context
            val expectedBitmap = try {
                ContextCompat.getDrawable(context, id)!!.tinted(tint, tintMode).toBitmap()
            } catch (e: Resources.NotFoundException) {
                VectorDrawableCompat.create(context.resources, id, null)!!.toBitmap()
            } catch (ignore : Exception) {
                null
            }
            return view is ImageView && view.drawable.toBitmap().sameAs(expectedBitmap)
        }

        private fun Drawable.tinted(@ColorInt tintColor: Int? = null, tintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN): Drawable {
            if (tintColor == null)
                return this
            return apply {
                setColorFilter(tintColor, tintMode)
            }
        }
    }
}
