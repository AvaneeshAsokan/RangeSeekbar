package com.laymancodes.rangeseekbarapp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.laymanCodes.rangeSeekbar.listener.RangeChangeListener
import com.laymanCodes.rangeSeekbar.px
import com.laymancodes.rangeseekbarapp.databinding.LayoutMainBinding

class   MainActivity: AppCompatActivity(), RangeChangeListener {
    private val TAG = MainActivity::class.java.canonicalName

    private var _binding: LayoutMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = LayoutMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.rangeBar.apply {
            setMin(0)
            setMax(1000)
            setRangeChangeListener(this@MainActivity)
        }

        binding.rangeBarView.apply {
            setUseIntrinsicSize(true)
//            setBarHeight(40.px.toFloat())
            setThumbGravity(Gravity.BOTTOM)
            setLeftThumbDrawable(resources.getDrawable(R.drawable.ic_triangle_green, context?.theme))
            setRightThumbDrawable(resources.getDrawable(R.drawable.ic_triangle_red, context?.theme))
            setTrackColor(resources.getColor(R.color.purple_200, context?.theme))
            setTrackHeight(-1)  //  for match_parent
        }
    }

    /*private suspend fun moveMin() {
        with(binding.rangeBarView) {
            while (getChosenMin() < getRangeMax()/2) {
                delay(400)
                setChosenMin(getChosenMin() + 5)
                yield()
                Log.d(TAG, "moveMin: running with ${getChosenMax()}")
            }
        }
    }

    private suspend fun moveMax() {
        with(binding.rangeBarView) {
            while (getChosenMax() > getRangeMax()/2) {
                delay(400)
                setChosenMax(getChosenMax() - 5)
                yield()
                Log.d(TAG, "moveMax: running with ${getChosenMax()}")
            }
        }
    }*/

    override fun onRangeChange(min: Int, max: Int) {
        Log.d(TAG, "onRangeChange: min = $min, max = $max")
    }
}