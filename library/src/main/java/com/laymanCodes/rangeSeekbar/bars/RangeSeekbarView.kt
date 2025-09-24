/*
 * Project Name: RangeSeekBar
 * Created by: Avaneesh Asokan
 * Last Modified: 23/09/2025, 23:16
 */

package com.laymanCodes.rangeSeekbar.bars

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.core.content.withStyledAttributes
import com.laymanCodes.rangeSeekbar.R
import com.laymanCodes.rangeSeekbar.enum.TouchTargets
import com.laymanCodes.rangeSeekbar.px
import kotlin.math.roundToInt

public class RangeSeekbarView : View {

    private val TAG = RangeSeekbarView::class.java.canonicalName

    constructor(context: Context) : super(context) {
        initialize(context, null)
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        this.cornerRadius = 5.px.toFloat()
        this.barPadding = 10.px.toFloat()
        this.trackHeight = 10.px
        getAttributeFromXml(context, attrs)
        initSetup()
    }

    @IntDef(flag = true, value = [
        Gravity.TOP, Gravity.BOTTOM, Gravity.CENTER
    ])
    @Retention(AnnotationRetention.SOURCE)
    annotation class GravityInt

    private var leftThumbDrawable: Drawable? = null
    private var rightThumbDrawable: Drawable? = null
    private var barDrawable: Drawable? = null
    private var leftThumbWidth: Int = 20.px
    private var rightThumbWidth: Int = 20.px
    private var leftThumbHeight: Int = 20.px
    private var rightThumbHeight: Int = 20.px
    private var gap: Int = 0
    private var leftThumbTint: Int? = null
    private var rightThumbTint: Int? = null
    private var enablePushThumb: Boolean = false
    private var useIntrinsicSize: Boolean = false
    private var thumbGravity = Gravity.CENTER

    private var cornerRadius: Float = 20.px.toFloat()
    private var barPadding: Float = 10.px.toFloat()
    private var trackHeight: Int = 30.px
    private var barColor: Int = Color.GRAY
    private var trackColor: Int = Color.BLACK
    private var rangeMin: Int = 0               //  min range choose-able
    private var rangeMax: Int = 1000             //  max range choose-able
    private var chosenMin: Float = rangeMin.toFloat()        //  actual chosen min value
    private var chosenMax: Float = rangeMax.toFloat()        //  actual chosen max value

    private var disableLeftThumbTouch: Boolean = false

    private var disableRightThumbTouch: Boolean = false

    private var currentTouchTarget: TouchTargets = TouchTargets.none

    private lateinit var barPaint: Paint
    private lateinit var barRect: RectF
    private lateinit var leftThumbRect: RectF
    private lateinit var leftThumbPaint: Paint
    private lateinit var rightThumbRect: RectF
    private lateinit var rightThumbPaint: Paint

    private fun initSetup() {
        barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        leftThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rightThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        barRect = RectF()
        leftThumbRect = RectF()
        rightThumbRect = RectF()
    }


    fun getTrackHeight() = trackHeight

    fun getRangeMin() = rangeMin

    fun getRangeMax() = rangeMax

    fun getChosenMin() = chosenMin

    fun getChosenMax() = chosenMax

    fun isUseIntrinsicSize() = useIntrinsicSize

    fun getGravity() = thumbGravity

    fun getLThumbWidth() = leftThumbWidth

    fun getRThumbWidth() = rightThumbWidth

    fun getLThumbHeight() = leftThumbHeight

    fun getRThumbHeight() = rightThumbHeight

    fun getTrackColor() = trackColor

    fun isLeftTouchDisabled() = disableLeftThumbTouch

    fun isRightTouchDisabled() = disableRightThumbTouch

    fun setTrackHeight(newHeight: Int) {
        trackHeight = newHeight
    }

    fun setRangeMin(rangeMin: Int) {
        this.rangeMin = rangeMin
    }

    fun setRangeMax(rangeMax: Int) {
        this.rangeMax = rangeMax
    }

    fun setChosenMin(chosenMin: Float) {
        this.chosenMin = chosenMin
        invalidate()
    }

    fun setChosenMax(chosenMax: Float) {
        this.chosenMax = chosenMax
        invalidate()
    }

    fun setUseIntrinsicSize(useIntrinsicSize: Boolean) {
        this.useIntrinsicSize = useIntrinsicSize
    }

    fun setThumbGravity(@GravityInt gravity: Int) {
        thumbGravity = gravity
    }

    fun setLThumbWidth(width: Int) {
        leftThumbWidth = width
    }

    fun setRThumbWidth(width: Int) {
        rightThumbWidth = width
    }

    fun setLThumbHeight(height: Int) {
        leftThumbHeight = height
    }

    fun setRThumbHeight(height: Int) {
        rightThumbHeight = height
    }

    fun setTrackColor(@ColorInt color: Int) {
        trackColor = color
    }

    fun setLeftThumbDrawable(drawable: Drawable) {
        leftThumbDrawable = drawable
    }

    fun setRightThumbDrawable(drawable: Drawable) {
        rightThumbDrawable = drawable
    }

    fun disableTouch(disable: Boolean){
        disableLeftThumbTouch = disable
        disableRightThumbTouch = disable
    }

    fun disableLeftThumbTouch(disable: Boolean){
        disableLeftThumbTouch = disable
    }

    fun disableRightThumbTouch(disable: Boolean){
        disableRightThumbTouch = disable
    }

    private fun getAttributeFromXml(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.RangeSeekbar) {
            leftThumbDrawable = getDrawable(R.styleable.RangeSeekbar_leftThumbDrawable)
            rightThumbDrawable = getDrawable(R.styleable.RangeSeekbar_rightThumbDrawable)
            barDrawable = getDrawable(R.styleable.RangeSeekbar_barDrawable)

            leftThumbWidth = getDimensionPixelSize(R.styleable.RangeSeekbar_leftThumbWidth, 20.px)
            rightThumbWidth = getDimensionPixelSize(R.styleable.RangeSeekbar_rightThumbWidth, 20.px)
            leftThumbHeight = getDimensionPixelSize(R.styleable.RangeSeekbar_leftThumbHeight, 20.px)
            rightThumbHeight = getDimensionPixelSize(R.styleable.RangeSeekbar_rightThumbHeight, 20.px)

            trackHeight = getLayoutDimension(R.styleable.RangeSeekbar_rangeTrackHeight, 20.px)
            barPadding = getDimensionPixelSize(R.styleable.RangeSeekbar_barPadding, 10.px).toFloat()
            barColor = getColor(R.styleable.RangeSeekbar_barColor, Color.GRAY)
            trackColor = getColor(R.styleable.RangeSeekbar_trackColor, Color.BLACK)

            gap = getDimensionPixelSize(R.styleable.RangeSeekbar_gap, 5.px)

            if (hasValue(R.styleable.RangeSeekbar_leftThumbTintColor)) {
                leftThumbTint = getColor(R.styleable.RangeSeekbar_leftThumbTintColor, Color.CYAN)
            }
            if (hasValue(R.styleable.RangeSeekbar_rightThumbTintColor)) {
                rightThumbTint = getColor(R.styleable.RangeSeekbar_rightThumbTintColor, Color.CYAN)
            }

            enablePushThumb = getBoolean(R.styleable.RangeSeekbar_enablePushThumb, false)
            disableLeftThumbTouch = getBoolean(R.styleable.RangeSeekbar_disableLeftThumbTouch, false)
            disableRightThumbTouch = getBoolean(R.styleable.RangeSeekbar_disableRightThumbTouch, false)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setupTrack(canvas)
        setupLeftThumb(canvas)
        setupRightThumb(canvas)
    }

    private fun setupTrack(
        canvas: Canvas,
    ) {
        barPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = trackColor
        }

        if (trackHeight == -1){
            trackHeight = height
        }

        barRect.apply {
            left = barPadding
            top = (height - trackHeight) * .5f
            right = width.toFloat() - barPadding
            bottom = (trackHeight + height) * .5f
        }

        drawTrack(canvas, barPaint, barRect)
    }

    private fun setupLeftThumb(
        canvas: Canvas,
    ) {
        leftThumbPaint.color = leftThumbTint ?: Color.CYAN

        val thumbCenterX = getOnBar(chosenMin.roundToInt())

        val currentThumbDrawWidth = if (leftThumbDrawable != null) {
            if (useIntrinsicSize) {
                leftThumbDrawable!!.intrinsicWidth.toFloat()
            } else {
                leftThumbWidth.toFloat() // from attribute R.styleable.RangeSeekbar_leftThumbWidth
            }
        } else {
            0.toFloat() // from attribute R.styleable.RangeSeekbar_leftThumbWidth
        }
        val currentThumbDrawHeight = if (leftThumbDrawable != null) {
            if (useIntrinsicSize) {
                leftThumbDrawable!!.intrinsicHeight.toFloat()
            } else {
                leftThumbHeight.toFloat() // from attribute R.styleable.RangeSeekbar_leftThumbHeight
            }
        } else {
            0.toFloat() // from attribute R.styleable.RangeSeekbar_leftThumbHeight
        }
        
        val actualThumbTop: Float
        val actualThumbBottom: Float

        when (thumbGravity) {
            Gravity.TOP -> {
                actualThumbTop = barRect.top
                actualThumbBottom = barRect.top + currentThumbDrawHeight
            }
            Gravity.BOTTOM -> {
                actualThumbBottom = barRect.bottom
                actualThumbTop = barRect.bottom - currentThumbDrawHeight
            }
            Gravity.CENTER -> { // Also Gravity.CENTER_VERTICAL or default
                val barCenterY = barRect.centerY()
                actualThumbTop = barCenterY - currentThumbDrawHeight / 2f
                actualThumbBottom = barCenterY + currentThumbDrawHeight / 2f
            }
            else -> { // Fallback, though thumbGravity is annotated and defaulted
                val barCenterY = barRect.centerY()
                actualThumbTop = barCenterY - currentThumbDrawHeight / 2f
                actualThumbBottom = barCenterY + currentThumbDrawHeight / 2f
//                Log.w(TAG, "setupLeftThumb: Unknown thumbGravity '$thumbGravity', defaulting to CENTER")
            }
        }

        val actualThumbLeft = thumbCenterX - currentThumbDrawWidth / 2f
        val actualThumbRight = thumbCenterX + currentThumbDrawWidth / 2f
        
        // This rect is used for touch detection and for drawing the fallback shape.
        leftThumbRect.set(actualThumbLeft, actualThumbTop, actualThumbRight, actualThumbBottom)

        leftThumbDrawable?.let { leftThumb ->
            leftThumb.setBounds(
                leftThumbRect.left.toInt(),
                leftThumbRect.top.toInt(),
                leftThumbRect.right.toInt(),
                leftThumbRect.bottom.toInt()
            )
            leftThumbTint?.let { tintColor ->
                leftThumb.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
            } ?: run {
                // Otherwise, clear the color filter to use the drawable's original color
                leftThumb.clearColorFilter()
            }
            leftThumb.draw(canvas)
        } ?: run {
            // Fallback: draw a rect using the bounds now correctly set in leftThumbRect
            canvas.drawRect(leftThumbRect, leftThumbPaint)
        }
//        Log.d(TAG, "setupLeftThumb: getMinSelected = ${getMinSelected()}")
    }

    private fun setupRightThumb(
        canvas: Canvas,
    ) {
        rightThumbPaint.color = rightThumbTint ?: Color.CYAN

        // 1. Calculate horizontal center position for the thumb
        val thumbCenterX = getOnBar(chosenMax.roundToInt())

        // 2. Determine thumb's effective drawing width and height
        val currentThumbDrawWidth = if (rightThumbDrawable != null) {
            if (useIntrinsicSize) {
                rightThumbDrawable!!.intrinsicWidth.toFloat()
            } else {
                rightThumbWidth.toFloat() // from attribute R.styleable.RangeSeekbar_rightThumbWidth
            }
        } else {
            0.toFloat() // from attribute R.styleable.RangeSeekbar_rightThumbWidth
        }
        val currentThumbDrawHeight = if (rightThumbDrawable != null) {
            if (useIntrinsicSize) {
                rightThumbDrawable!!.intrinsicHeight.toFloat()
            } else {
                rightThumbHeight.toFloat() // from attribute R.styleable.RangeSeekbar_rightThumbHeight
            }
        } else {
            0.toFloat() // from attribute R.styleable.RangeSeekbar_rightThumbHeight
        }

        // 3. Calculate vertical position based on thumbGravity and barRect
        val actualThumbTop: Float
        val actualThumbBottom: Float

        when (thumbGravity) {
            Gravity.TOP -> {
                actualThumbTop = barRect.top
                actualThumbBottom = barRect.top + currentThumbDrawHeight
            }
            Gravity.BOTTOM -> {
                actualThumbBottom = barRect.bottom
                actualThumbTop = barRect.bottom - currentThumbDrawHeight
            }
            Gravity.CENTER -> { // Also Gravity.CENTER_VERTICAL or default
                val barCenterY = barRect.centerY()
                actualThumbTop = barCenterY - currentThumbDrawHeight / 2f
                actualThumbBottom = barCenterY + currentThumbDrawHeight / 2f
            }
            else -> { // Fallback, though thumbGravity is annotated and defaulted
                val barCenterY = barRect.centerY()
                actualThumbTop = barCenterY - currentThumbDrawHeight / 2f
                actualThumbBottom = barCenterY + currentThumbDrawHeight / 2f
//                Log.w(TAG, "setupRightThumb: Unknown thumbGravity '$thumbGravity', defaulting to CENTER")
            }
        }

        // 4. Calculate actual horizontal bounds for drawing
        val actualThumbLeft = thumbCenterX - currentThumbDrawWidth / 2f
        val actualThumbRight = thumbCenterX + currentThumbDrawWidth / 2f

        // 5. Update rightThumbRect to reflect the actual calculated bounds.
        // This rect is used for touch detection and for drawing the fallback shape.
        rightThumbRect.set(actualThumbLeft, actualThumbTop, actualThumbRight, actualThumbBottom)

        // 6. Draw the thumb drawable or a fallback rectangle
        rightThumbDrawable?.let { rightThumb ->
            rightThumb.setBounds(
                rightThumbRect.left.toInt(),
                rightThumbRect.top.toInt(),
                rightThumbRect.right.toInt(),
                rightThumbRect.bottom.toInt()
            )
            rightThumbTint?.let { tintColor ->
                rightThumb.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
            } ?: run {
                // Otherwise, clear the color filter to use the drawable's original color
                rightThumb.clearColorFilter()
            }
            rightThumb.draw(canvas)
        } ?: run {
            // Fallback: draw a rect using the bounds now correctly set in rightThumbRect
            canvas.drawRect(rightThumbRect, rightThumbPaint)
        }
//        Log.d(TAG, "setupRightThumb: getMaxSelected = ${getMaxSelected()}")
    }

    private fun drawTrack(canvas: Canvas, paint: Paint, rectF: RectF) {
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }

    /**
     * gets the position of the value on the bar based on the range chosen for min and max.
     */
    private fun getOnBar(value: Int): Float {
        //  find the position of the value based on the width of the bar
//        return (((value.coerceIn(rangeMin, rangeMax).toFloat() / rangeMax) * barRect.width()) + barRect.left)
        val currentVal = value.coerceIn(rangeMin, rangeMax)
        val range = (rangeMax - rangeMin).toFloat()

        if (range == 0F){
            return barRect.left
        }

        val percentage = (currentVal - rangeMin) / range
        return (percentage * barRect.width()) + barRect.left
    }

    private fun getActualValueFromPosition(xPosition: Float): Float {
        /*return (((xPosition - barRect.left) * rangeMax) / barRect.width()).coerceIn(
            rangeMin.toFloat(),
            rangeMax.toFloat()
        )*/

        val coercedX = xPosition.coerceIn(barRect.left, barRect.right)
        val range = (rangeMax - rangeMin).toFloat()

        if (barRect.width() == 0F){
            return rangeMin.toFloat()
        }

        val percentage = (coercedX - barRect.left) / barRect.width()
        return (percentage * range) + rangeMin

    }

    fun getMinSelected(): Int = getActualValueFromPosition(leftThumbRect.centerX()).roundToInt()

    fun getMaxSelected(): Int = getActualValueFromPosition(rightThumbRect.centerX()).roundToInt()


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)

        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                currentTouchTarget = findPressedThumb(event)
//                Log.d(TAG, "onTouchEvent: touched = ${currentTouchTarget.name}")
            }

            MotionEvent.ACTION_MOVE -> {
                //  in case touch is disabled
                if (disableLeftThumbTouch && currentTouchTarget == TouchTargets.leftThumb ||
                    disableRightThumbTouch && currentTouchTarget == TouchTargets.rightThumb) {
                    return true
                }

                if (currentTouchTarget == TouchTargets.leftThumb) {
                    chosenMin = getActualValueFromPosition(event.x).coerceIn(rangeMin.toFloat(), chosenMax)
                    invalidate()
                } else if (currentTouchTarget == TouchTargets.rightThumb) {
                    chosenMax = getActualValueFromPosition(event.x).coerceIn(chosenMin, rangeMax.toFloat())
                    invalidate()
                }

//                Log.d(TAG, "onTouchEvent: chosen min max = $chosenMin, $chosenMax")
//                Log.d(TAG, "onTouchEvent: selected ${getMinSelected()}, ${getMaxSelected()}")
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                currentTouchTarget = TouchTargets.none
            }

        }

        return true
    }

    private fun findPressedThumb(event: MotionEvent): TouchTargets {
        if (event.x in (leftThumbRect.left - 10)..leftThumbRect.right + 10)
            return TouchTargets.leftThumb
        else if (event.x in (rightThumbRect.left - 10)..rightThumbRect.right + 10)
            return TouchTargets.rightThumb

        return TouchTargets.bar
    }
}
