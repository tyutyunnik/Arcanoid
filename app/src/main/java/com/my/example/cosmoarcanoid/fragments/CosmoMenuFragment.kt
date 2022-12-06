package com.my.example.cosmoarcanoid.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.my.example.cosmoarcanoid.R
import com.my.example.cosmoarcanoid.databinding.FragmentCosmoMenuBinding

class CosmoMenuFragment : Fragment(R.layout.fragment_cosmo_menu) {

    private lateinit var binding: FragmentCosmoMenuBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var cont = true

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCosmoMenuBinding.bind(view)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        sharedPreferences =
            requireActivity().getSharedPreferences("arcSharedP", AppCompatActivity.MODE_PRIVATE)
        cont = sharedPreferences.getBoolean("continue", false)

        binding.startBtn.setOnClickListener {
            findNavController().navigate(R.id.action_cosmoMenuFragment_to_arcGameFragment)
        }

        binding.contBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_cosmoMenuFragment_to_arcGameFragment
            )
            sharedPreferences.edit().putBoolean("continue", true).apply()
        }

        binding.quitBtn.setOnClickListener {
            quitDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    quitDialog()
                }
            }
        )
    }

    private fun quitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Quit?")
            .setMessage("Sure?")
            .setPositiveButton("Yes") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }
}