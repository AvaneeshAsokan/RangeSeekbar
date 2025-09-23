/*
 * Project Name: RangeSeekBar
 * Created by: Avaneesh Asokan
 * Last Modified: 07/06/2025, 12:07
 */

package com.laymanCodes.rangeSeekbar

import android.content.res.Resources
import android.util.TypedValue


/**
 * converts dp to px
 */
val Float.px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

/**
 * converts dp to px
 */
val Int.px: Int
    get() = toFloat().px.toInt()

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()