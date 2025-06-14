package com.laymanCodes.rangeSeekbar.bars

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.method.Touch
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import com.laymanCodes.rangeSeekbar.R
import com.laymanCodes.rangeSeekbar.enum.TouchTargets
import com.laymanCodes.rangeSeekbar.px
import kotlin.math.roundToInt

class RangeSeekbarView : View {

    private val TAG = RangeSeekbarView::class.java.canonicalName

    constructor(context: Context) : super(context)

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
        this.barHeight = 10.px.toFloat()
        getAttributeFromXml(context, attrs)
        initSetup()
    }

    private var leftThumbDrawable: Drawable? = null
    private var rightThumbDrawable: Drawable? = null
    private var leftThumbWidth: Int = 20.px
    private var rightThumbWidth: Int = 20.px
    private var leftThumbHeight: Int = 20.px
    private var rightThumbHeight: Int = 20.px
    private var gap: Int = 0
    private var leftThumbTint: Int? = null
    private var rightThumbTint: Int? = null
    private var enablePushThumb: Boolean = false

    private var cornerRadius: Float = 20.px.toFloat()
    private var barPadding: Float = 10.px.toFloat()
    private var barHeight: Float = 30.px.toFloat()
    private var rangeMin: Int = 0               //  min range choose-able
    private var rangeMax: Int = 1000             //  max range choose-able
    private var chosenMin: Float = rangeMin.toFloat()        //  actual chosen min value
    private var chosenMax: Float = rangeMax.toFloat()        //  actual chosen max value

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

    private fun getAttributeFromXml(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.RangeSeekbar) {
            leftThumbDrawable = getDrawable(R.styleable.RangeSeekbar_leftThumbDrawable)
            rightThumbDrawable = getDrawable(R.styleable.RangeSeekbar_rightThumbDrawable)

            leftThumbWidth = getDimensionPixelSize(R.styleable.RangeSeekbar_leftThumbWidth, 20.px)
            rightThumbWidth = getDimensionPixelSize(R.styleable.RangeSeekbar_rightThumbWidth, 20.px)
            leftThumbHeight = getDimensionPixelSize(R.styleable.RangeSeekbar_leftThumbHeight, 20.px)
            rightThumbHeight =
                getDimensionPixelSize(R.styleable.RangeSeekbar_rightThumbHeight, 20.px)

            gap = getDimensionPixelSize(R.styleable.RangeSeekbar_gap, 5.px)

            leftThumbTint = getColor(R.styleable.RangeSeekbar_leftThumbTintColor, Color.CYAN)
            rightThumbTint = getColor(R.styleable.RangeSeekbar_rightThumbTintColor, Color.CYAN)

            enablePushThumb = getBoolean(R.styleable.RangeSeekbar_enablePushThumb, false)
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
            color = context.getColor(R.color.track_gray)
        }

        barRect.apply {
            left = barPadding
            top = (height - barHeight) * .5f
            right = width.toFloat() - barPadding
            bottom = (barHeight + height) * .5f
        }

        drawTrack(canvas, barPaint, barRect)
    }

    private fun setupLeftThumb(
        canvas: Canvas,
    ) {
        leftThumbPaint.color = leftThumbTint ?: Color.CYAN

        leftThumbRect.apply {
            left = getOnBar(chosenMin.roundToInt()) - leftThumbWidth.toFloat()/2
            top = 0f
            right = left + leftThumbWidth.toFloat()
            bottom = height.toFloat()
        }

        //  when calculating the actual left thumb value we must minus with the visual offset of barRect.left

        canvas.drawRect(leftThumbRect, leftThumbPaint)
        Log.d(TAG, "setupLeftThumb: getMinSelected = ${getMinSelected()}")
    }

    private fun setupRightThumb(
        canvas: Canvas,
    ) {
        rightThumbPaint.color = rightThumbTint ?: Color.CYAN

        rightThumbRect.apply {
            left = getOnBar(chosenMax.roundToInt()) - rightThumbWidth.toFloat()/2
            top = 0f
            right = getOnBar(chosenMax.roundToInt()) + rightThumbWidth.toFloat()/2
            bottom = height.toFloat()
        }

        canvas.drawRect(rightThumbRect, rightThumbPaint)
        Log.d(TAG, "setupRightThumb: getMaxSelected = ${getMaxSelected()}")
    }

    private fun drawTrack(canvas: Canvas, paint: Paint, rectF: RectF) {
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }

    /**
     * gets the position of the value on the bar based on the range chosen for min and max.
     */
    private fun getOnBar(value: Int): Float {
        //  find the position of the value based on the width of the bar
        if (value in rangeMin..rangeMax) {
            return (((value.toFloat() / rangeMax) * barRect.width()) + barRect.left)
        } else {
            throw IllegalArgumentException("Chosen value is out of range.")
        }
    }

    private fun getActualValueFromPosition(xPosition: Float): Float {
        return (((xPosition - barRect.left) * rangeMax) / barRect.width()).coerceIn(
            rangeMin.toFloat(),
            rangeMax.toFloat()
        )
    }

    fun getMinSelected(): Int =
        (((leftThumbRect.centerX() - barRect.left) * rangeMax) / barRect.width()).roundToInt()

    fun getMaxSelected(): Int =
        (((rightThumbRect.centerX() - barRect.left) * rangeMax) / barRect.width()).roundToInt()


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)

        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                currentTouchTarget = findPressedThumb(event)
                Log.d(TAG, "onTouchEvent: touched = ${currentTouchTarget.name}")
            }

            MotionEvent.ACTION_MOVE -> {
                if (currentTouchTarget == TouchTargets.leftThumb) {
                    chosenMin = getActualValueFromPosition(event.x).coerceIn(rangeMin.toFloat(), chosenMax)
                    invalidate()
                } else if (currentTouchTarget == TouchTargets.rightThumb) {
                    chosenMax = getActualValueFromPosition(event.x).coerceIn(chosenMin, rangeMax.toFloat())
                    invalidate()
                }

                Log.d(TAG, "onTouchEvent: chosen min max = $chosenMin, $chosenMax")
                Log.d(TAG, "onTouchEvent: selected ${getMinSelected()}, ${getMaxSelected()}")
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