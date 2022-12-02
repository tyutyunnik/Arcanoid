package com.my.example.cosmoarcanoid.fragments

import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.my.example.cosmoarcanoid.R
import com.my.example.cosmoarcanoid.databinding.FragmentArcGameBinding

class ArcGameFragment : Fragment() {

    private lateinit var binding: FragmentArcGameBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var cont = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArcGameBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("arcSharedP", AppCompatActivity.MODE_PRIVATE)
        cont = sharedPreferences.getBoolean("continue", false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.arcGame.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.arcGame.resume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.arcGame.saveState()
        sharedPreferences.edit().putBoolean("continue", true).apply()
        binding.arcGame.surfaceChanged(binding.arcGame.holder, PixelFormat.RGB_565, 0, 0)
        binding.arcGame.invalidate()
    }
}