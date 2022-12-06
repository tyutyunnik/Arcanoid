package com.my.example.cosmoarcanoid.fragments

import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.my.example.cosmoarcanoid.R
import com.my.example.cosmoarcanoid.databinding.FragmentArcGameBinding

class ArcGameFragment : Fragment(R.layout.fragment_arc_game) {

    private lateinit var binding: FragmentArcGameBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var cont = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArcGameBinding.bind(view)

        sharedPreferences =
            requireActivity().getSharedPreferences("arcSharedP", AppCompatActivity.MODE_PRIVATE)
        cont = sharedPreferences.getBoolean("continue", false)

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
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