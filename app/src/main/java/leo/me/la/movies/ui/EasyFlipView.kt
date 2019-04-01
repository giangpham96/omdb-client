package leo.me.la.movies.ui

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.isGone
import androidx.core.view.isVisible
import leo.me.la.movies.R
import leo.me.la.movies.util.toPx

/**
 * A quick and easy flip view through which you can create views with two sides like credit cards,
 * poker cards, flash cards etc.
 *
 * Add EasyFlipView into your XML layouts with two direct children
 * views and you are done!
 *
 * ORIGINAL FROM:
 * @author Wajahat Karim (http://wajahatkarim.com)
 * @version 2.0.4 18/12/2017
 * For more information, check http://github.com/wajahatkarim3/EasyFlipView
 *
 * COMPLETED BY:
 * @author Marcel Holter, Ville Raisanen (Zalando SE)
 */
class EasyFlipView : FrameLayout {

    /**
     * The Flip Animation Listener for animations and flipping complete listeners
     */
    interface OnFlipAnimationListener {

        /**
         * Called when flip animation is completed.
         *
         * @param easyFlipView The current EasyFlipView instance
         * @param newCurrentSide After animation, the new side of the view. Either can be
         * FlipState.FRONT_SIDE or FlipState.BACK_SIDE
         */
        fun onViewFlipCompleted(easyFlipView: EasyFlipView, newCurrentSide: FlipState)

    }

    companion object {

        private const val DEFAULT_FLIP_DURATION = 400
        private const val DISTANCE_CAMERA = 8000

        private val maxClickDistance = 100f.toPx()

    }

    enum class FlipState {
        FRONT_SIDE, BACK_SIDE
    }

    object FlipType {
        var HORIZONTAL = 0
        var VERTICAL = 1
    }

    private val animFlipHorizontalOutId = R.animator.animation_horizontal_flip_out
    private val animFlipHorizontalInId = R.animator.animation_horizontal_flip_in
    private val animFlipVerticalOutId = R.animator.animation_vertical_flip_out
    private val animFlipVerticalInId = R.animator.animation_vertical_flip_in

    private var setRightOut: AnimatorSet? = null
    private var setLeftIn: AnimatorSet? = null
    private var setTopOut: AnimatorSet? = null
    private var setBottomIn: AnimatorSet? = null
    private var isBackVisible = false
    private var cardFrontLayout: View? = null
    private var cardBackLayout: View? = null
    private var flipType = FlipType.VERTICAL

    private var x1: Float = 0.toFloat()
    private var y1: Float = 0.toFloat()

    private var flipDuration: Int = 0

    var isFlipOnTouch: Boolean = false

    var isFlipEnabled: Boolean = false

    var currentFlipState = FlipState.FRONT_SIDE
        private set

    /**
     * Returns the current OnFlipAnimationListener. Null if no listener is set.
     * @return Returns the current OnFlipAnimationListener. Null if no listener is set.
     */
    /**
     * Sets the OnFlipAnimationListener for the view
     * @param onFlipListener
     */
    var onFlipListener: OnFlipAnimationListener? = null

    /**
     * Returns true if the front side of flip view is visible.
     *
     * @return true if the front side of flip view is visible.
     */
    val isFrontSide: Boolean
        get() = currentFlipState == FlipState.FRONT_SIDE

    /**
     * Returns true if the back side of flip view is visible.
     *
     * @return true if the back side of flip view is visible.
     */
    val isBackSide: Boolean
        get() = currentFlipState == FlipState.BACK_SIDE

    /**
     * Returns true if the Flip Type of animation is FlipType.HORIZONTAL?
     */
    val isHorizontalType: Boolean
        get() = flipType == FlipType.HORIZONTAL

    /**
     * Returns true if the Flip Type of animation is FlipType.VERTICAL?
     */
    val isVerticalType: Boolean
        get() = flipType == FlipType.VERTICAL

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // Setting Defaul Values
        isFlipOnTouch = true
        flipDuration = DEFAULT_FLIP_DURATION
        isFlipEnabled = true
        flipType = FlipType.VERTICAL

        // Check for the attributes
        if (attrs != null) {
            // Attribute initialization
            val attrArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.EasyFlipView,
                0,
                0
            )
            try {
                isFlipOnTouch = attrArray.getBoolean(R.styleable.EasyFlipView_flipOnTouch, true)
                flipDuration = attrArray.getInt(R.styleable.EasyFlipView_flipDuration, DEFAULT_FLIP_DURATION)
                isFlipEnabled = attrArray.getBoolean(R.styleable.EasyFlipView_flipEnabled, true)
                flipType = attrArray.getInt(R.styleable.EasyFlipView_flipType, FlipType.HORIZONTAL)
            } finally {
                attrArray.recycle()
            }
        }
        loadAnimations()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount > 2) {
            throw IllegalStateException("EasyFlipView can host only two direct children!")
        }

        findViews()
        changeCameraDistance()
    }

    override fun addView(v: View, pos: Int, params: ViewGroup.LayoutParams) {
        if (childCount == 2) {
            throw IllegalStateException("EasyFlipView can host only two direct children!")
        }
        super.addView(v, pos, params)

        findViews()
        changeCameraDistance()
    }

    override fun removeView(v: View) {
        super.removeView(v)

        findViews()
    }

    override fun removeAllViewsInLayout() {
        super.removeAllViewsInLayout()

        // Reset the state
        currentFlipState = FlipState.FRONT_SIDE

        findViews()
    }

    private fun findViews() {
        // Invalidation since we use this also on removeView
        cardBackLayout = null
        cardFrontLayout = null

        val children = childCount
        if (children < 1) {
            return
        }

        if (children < 2) {
            // Only invalidate flip state if we have a single child
            currentFlipState = FlipState.FRONT_SIDE
            cardFrontLayout = getChildAt(0)
        } else if (children == 2) {
            cardFrontLayout = getChildAt(1)
            cardBackLayout = getChildAt(0)
        }

        cardFrontLayout!!.isVisible = true
        cardBackLayout?.run {
            isGone = true
        }
    }

    private fun loadAnimations() {
        if (flipType == FlipType.HORIZONTAL) {
            setRightOut = AnimatorInflater.loadAnimator(this.context, animFlipHorizontalOutId) as AnimatorSet
            setLeftIn = AnimatorInflater.loadAnimator(this.context, animFlipHorizontalInId) as AnimatorSet
            if (setRightOut == null || setLeftIn == null) {
                throw RuntimeException(
                    "No Animations Found! Please set Flip in and Flip out animation Ids."
                )
            }
            with(setRightOut!!) {
                removeAllListeners()
                doOnEnd {
                    if (currentFlipState == FlipState.FRONT_SIDE) {
                        cardBackLayout!!.visibility = View.GONE
                        cardFrontLayout!!.visibility = View.VISIBLE
                        if (onFlipListener != null)
                            onFlipListener!!.onViewFlipCompleted(
                                this@EasyFlipView,
                                FlipState.FRONT_SIDE
                            )
                    } else {
                        cardBackLayout!!.visibility = View.VISIBLE
                        cardFrontLayout!!.visibility = View.GONE

                        if (onFlipListener != null)
                            onFlipListener!!.onViewFlipCompleted(
                                this@EasyFlipView,
                                FlipState.BACK_SIDE
                            )
                    }
                }
            }
            setFlipDuration(flipDuration)
        } else {
            setTopOut =
                AnimatorInflater.loadAnimator(this.context, animFlipVerticalOutId) as AnimatorSet
            setBottomIn =
                AnimatorInflater.loadAnimator(this.context, animFlipVerticalInId) as AnimatorSet
            if (setTopOut == null || setBottomIn == null) {
                throw RuntimeException(
                    "No Animations Found! Please set Flip in and Flip out animation Ids."
                )
            }
            with(setTopOut!!) {
                removeAllListeners()
                doOnEnd {
                    if (currentFlipState == FlipState.FRONT_SIDE) {
                        cardBackLayout!!.visibility = View.GONE
                        cardFrontLayout!!.visibility = View.VISIBLE

                        if (onFlipListener != null)
                            onFlipListener!!.onViewFlipCompleted(
                                this@EasyFlipView,
                                FlipState.FRONT_SIDE
                            )
                    } else {
                        cardBackLayout!!.visibility = View.VISIBLE
                        cardFrontLayout!!.visibility = View.GONE

                        if (onFlipListener != null)
                            onFlipListener!!.onViewFlipCompleted(
                                this@EasyFlipView,
                                FlipState.BACK_SIDE
                            )
                    }
                }
            }
            setFlipDuration(flipDuration)
        }
    }

    private fun changeCameraDistance() {
        val scale = resources.displayMetrics.density * DISTANCE_CAMERA

        cardFrontLayout?.run {
            cameraDistance = scale
        }
        cardBackLayout?.run {
            cameraDistance = scale
        }
    }

    /**
     * Play the animation of flipping and flip the view for one side!
     */
    fun flipTheView() {
        if (!isFlipEnabled || childCount < 2) return

        if (flipType == FlipType.HORIZONTAL) {
            if (setRightOut!!.isRunning || setLeftIn!!.isRunning) return

            cardBackLayout!!.isVisible = true
            cardFrontLayout!!.isVisible = true

            if (currentFlipState == FlipState.FRONT_SIDE) {
                // From front to back
                setRightOut!!.setTarget(cardFrontLayout)
                setLeftIn!!.setTarget(cardBackLayout)
                setRightOut!!.start()
                setLeftIn!!.start()
                isBackVisible = true
                currentFlipState = FlipState.BACK_SIDE
            } else {
                // from back to front
                setRightOut!!.setTarget(cardBackLayout)
                setLeftIn!!.setTarget(cardFrontLayout)
                setRightOut!!.start()
                setLeftIn!!.start()
                isBackVisible = false
                currentFlipState = FlipState.FRONT_SIDE
            }
        } else {
            if (setTopOut!!.isRunning || setBottomIn!!.isRunning) return

            cardBackLayout!!.isVisible = true
            cardFrontLayout!!.isVisible = true

            if (currentFlipState == FlipState.FRONT_SIDE) {
                // From front to back
                setTopOut!!.setTarget(cardFrontLayout)
                setBottomIn!!.setTarget(cardBackLayout)
                setTopOut!!.start()
                setBottomIn!!.start()
                isBackVisible = true
                currentFlipState = FlipState.BACK_SIDE
            } else {
                // from back to front
                setTopOut!!.setTarget(cardBackLayout)
                setBottomIn!!.setTarget(cardFrontLayout)
                setTopOut!!.start()
                setBottomIn!!.start()
                isBackVisible = false
                currentFlipState = FlipState.FRONT_SIDE
            }
        }
    }

    /**
     * Flip the view for one side with or without animation.
     *
     * @param withAnimation true means flip view with animation otherwise without animation.
     */
    fun flipTheView(withAnimation: Boolean) {
        if (childCount < 2) return

        if (flipType == FlipType.HORIZONTAL) {
            if (!withAnimation) {
                val oldFlipDuration = flipDuration
                setFlipDuration(0)
                val oldFlipEnabled = isFlipEnabled
                isFlipEnabled = true

                flipTheView()

                setFlipDuration(oldFlipDuration)
                isFlipEnabled = oldFlipEnabled
            } else {
                flipTheView()
            }
        } else {
            if (!withAnimation) {
                setBottomIn!!.duration = 0
                setTopOut!!.duration = 0
                val oldFlipEnabled = isFlipEnabled
                isFlipEnabled = true

                flipTheView()

                setBottomIn!!.duration = flipDuration.toLong()
                setTopOut!!.duration = flipDuration.toLong()
                isFlipEnabled = oldFlipEnabled
            } else {
                flipTheView()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (isEnabled && isFlipOnTouch) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    y1 = event.y
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val x2 = event.x
                    val y2 = event.y
                    val dx = Math.abs(x2 - x1)
                    val dy = Math.abs(y2 - y1)
                    if (dx < maxClickDistance && dy < maxClickDistance) {
                        flipTheView()
                    }
                    return true
                }
            }
        } else {
            return super.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    /**
     * Returns duration of flip in milliseconds!
     *
     * @return duration in milliseconds
     */
    fun getFlipDuration(): Int {
        return flipDuration
    }

    /**
     * Sets the flip duration (in milliseconds)
     *
     * @param flipDuration duration in milliseconds
     */
    fun setFlipDuration(flipDuration: Int) {
        this.flipDuration = flipDuration
        if (flipType == FlipType.HORIZONTAL) {
            setRightOut!!.childAnimations[0].duration = flipDuration.toLong()
            setRightOut!!.childAnimations[1].startDelay = (flipDuration / 2).toLong()

            setLeftIn!!.childAnimations[1].duration = flipDuration.toLong()
            setLeftIn!!.childAnimations[2].startDelay = (flipDuration / 2).toLong()
        } else {
            setTopOut!!.childAnimations[0].duration = flipDuration.toLong()
            setTopOut!!.childAnimations[1].startDelay = (flipDuration / 2).toLong()

            setBottomIn!!.childAnimations[1].duration = flipDuration.toLong()
            setBottomIn!!.childAnimations[2].startDelay = (flipDuration / 2).toLong()
        }
    }

    /**
     * Sets the Flip Type of animation to FlipType.HORIZONTAL
     */
    fun setToHorizontalType() {
        flipType = FlipType.HORIZONTAL
    }

    /**
     * Sets the Flip Type of animation to FlipType.VERTICAL
     */
    fun setToVerticalType() {
        flipType = FlipType.VERTICAL
    }

}

