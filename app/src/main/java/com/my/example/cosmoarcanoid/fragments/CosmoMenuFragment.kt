package com.my.example.cosmoarcanoid.fragments

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.my.example.cosmoarcanoid.R
import com.my.example.cosmoarcanoid.databinding.FragmentCosmoMenuBinding

class CosmoMenuFragment : Fragment() {

    private lateinit var binding: FragmentCosmoMenuBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var cont = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCosmoMenuBinding.inflate(inflater, container, false)
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

//        requireActivity().onBackPressedDispatcher.addCallback(
//            requireActivity(), object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    quitDialog()
//                }
//            }
//        )

        return binding.root
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