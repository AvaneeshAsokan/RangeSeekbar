package com.laymanCodes.rangeSeekbar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.laymanCodes.rangeSeekbar.databinding.LayoutMainBinding
import com.laymanCodes.rangeSeekbar.listener.RangeChangeListener

class MainActivity: AppCompatActivity(), RangeChangeListener {
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

    }

    override fun onRangeChange(min: Int, max: Int) {
        Log.d(TAG, "onRangeChange: min = $min, max = $max")
    }
}