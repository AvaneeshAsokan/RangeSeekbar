package com.laymanCodes.rangeSeekbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.withStyledAttributes
import com.laymanCodes.rangeSeekbar.databinding.RangeSeekbarBinding
import com.laymanCodes.rangeSeekbar.listener.RangeChangeListener
import kotlin.math.roundToInt

class RangeSeekbar : ConstraintLayout {
    private val TAG = RangeSeekbar::class.java.canonicalName

    constructor(context: Context): super(context) {
        initRangeSeekbar(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ): super(context, attrs){
        getAttributeFromXml(context, attrs)
        initRangeSeekbar(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ): super(context, attrs, defStyleAttr) {
        getAttributeFromXml(context, attrs)
        initRangeSeekbar(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0): super(context, attrs, defStyleAttr,defStyleRes)

    private var _binding: RangeSeekbarBinding? = null
    private val binding: RangeSeekbarBinding get() = _binding!!

    //  visual attributes
    private var leftThumbDrawable: Drawable? = null
    private var rightThumbDrawable: Drawable? = null
    private var leftThumbWidth: Int? = 20.px
    private var rightThumbWidth: Int? = 20.px
    private var leftThumbHeight: Int? = 20.px
    private var rightThumbHeight: Int? = 20.px
    private var leftThumbTint: Int? = null
    private var rightThumbTint: Int? = null
    private var gap: Int = 0.px
    private var enablePushThumb: Boolean = false

    //  functional attributes
    private var min: Int = 0
    private var max: Int = 100
    private var leftThumbValue: Int = min
    private var rightThumbValue: Int = max

    private var rangeChangeListener: RangeChangeListener? = null

    fun setMin(min: Int){
        this.min = min
    }

    fun setMax(max: Int){
        this.max = max
    }

    fun setRangeChangeListener(rangeChangeListener: RangeChangeListener){
        this.rangeChangeListener = rangeChangeListener
    }

    private fun getAttributeFromXml(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.RangeSeekbar) {
            leftThumbDrawable = getDrawable(R.styleable.RangeSeekbar_leftThumbDrawable)
            rightThumbDrawable = getDrawable(R.styleable.RangeSeekbar_rightThumbDrawable)

            leftThumbWidth = getDimensionPixelSize(R.styleable.RangeSeekbar_leftThumbWidth, 20.px)
            rightThumbWidth = getDimensionPixelSize(R.styleable.RangeSeekbar_rightThumbWidth, 20.px)
            leftThumbHeight = getDimensionPixelSize(R.styleable.RangeSeekbar_leftThumbHeight, 20.px)
            rightThumbHeight = getDimensionPixelSize(R.styleable.RangeSeekbar_rightThumbHeight, 20.px)

            gap = getDimensionPixelSize(R.styleable.RangeSeekbar_gap, 5.px)

            leftThumbTint = getColor(R.styleable.RangeSeekbar_leftThumbTintColor, Color.CYAN)
            rightThumbTint = getColor(R.styleable.RangeSeekbar_rightThumbTintColor, Color.CYAN)

            enablePushThumb = getBoolean(R.styleable.RangeSeekbar_enablePushThumb, false)
        }
    }

    private fun initRangeSeekbar(context: Context) {
//        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        _binding = RangeSeekbarBinding.inflate(LayoutInflater.from(context), this, true)

        setAttributes()
        setupListeners()
        refreshDrawableState()
    }

    private fun setAttributes(){
        leftThumbDrawable?.let { leftDrawable ->
            binding.startThumb.setImageDrawable(leftDrawable)
        }
        rightThumbDrawable?.let { rightDrawable ->
            binding.endThumb.setImageDrawable(rightDrawable)
        }
        leftThumbTint?.let { lTint ->
            binding.startThumb.imageTintList = ColorStateList.valueOf(lTint)
        }
        rightThumbTint?.let { rTint ->
            binding.endThumb.imageTintList = ColorStateList.valueOf(rTint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        with(binding){
            startThumb.setOnTouchListener { v: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_MOVE) {
                    if (startThumb.x + event.x in 0.0f .. (endThumb.x - gap).toFloat()) {
                        startThumb.x += event.x
                    } else if (enablePushThumb && (startThumb.x + event.x) in (endThumb.x - gap).toFloat() .. (track.right - gap).toFloat()) {
                        endThumb.x = endThumb.x + event.x
                        startThumb.x = endThumb.x - gap
                    }
                } else if (event.action == MotionEvent.ACTION_UP) {
                    val totalPxInTrack = track.width
                    val totalRange = max - min

                    val leftThumbValue = ((startThumb.x / totalPxInTrack) * totalRange).roundToInt() + min
                    val rightThumbValue = ((endThumb.x / totalPxInTrack) * totalRange).roundToInt() + min

                    rangeChangeListener?.onRangeChange(leftThumbValue, rightThumbValue)
                }
                return@setOnTouchListener true
            }
            endThumb.setOnTouchListener { v: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_MOVE) {
                    if (endThumb.x + event.x in (startThumb.x + gap).toFloat() .. (track.right - 10.px).toFloat()) {
                        endThumb.x += event.x
                    } else if (enablePushThumb && (endThumb.x + event.x) in (0.0f + gap) .. (startThumb.x + gap)) {
                        startThumb.x = startThumb.x + event.x
                        endThumb.x = startThumb.x + gap
                    }
                } else if (event.action == MotionEvent.ACTION_UP) {
                    val totalPxInTrack = track.width
                    val totalRange = max - min

                    val leftThumbValue = ((startThumb.x / totalPxInTrack) * totalRange).roundToInt() + min
                    val rightThumbValue = ((endThumb.x / totalPxInTrack) * totalRange).roundToInt() + min

                    rangeChangeListener?.onRangeChange(leftThumbValue, rightThumbValue)
                }
                return@setOnTouchListener true
            }
        }
    }
}