package com.mstete.wnn.csmt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.mstete.wnn.csmt.R
import com.mstete.wnn.csmt.databinding.FragmentCosmoResultBinding

class CosmoResultFragment : Fragment(R.layout.fragment_cosmo_result) {
    private lateinit var binding: FragmentCosmoResultBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCosmoResultBinding.bind(view)

        binding.resultText.text = requireArguments().getString("result", "You Lose!")

        binding.playAgainBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.menuBtn.setOnClickListener {
            findNavController().popBackStack(R.id.cosmoMenuFragment, false)
        }
    }
}