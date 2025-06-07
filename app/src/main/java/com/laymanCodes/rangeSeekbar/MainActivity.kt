package com.laymanCodes.rangeSeekbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.laymanCodes.rangeSeekbar.databinding.LayoutMainBinding

class MainActivity: AppCompatActivity() {
    private var _binding: LayoutMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = LayoutMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}