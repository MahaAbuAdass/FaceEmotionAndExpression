package com.example.facerecognitionandemotion.di.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.facerecognitionandemotion.R
import com.example.facerecognitionandemotion.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.btnIn.setOnClickListener {
            navigateToCameraScreen()
        }

        binding.btnOut.setOnClickListener {
            navigateToCameraScreen()
        }
        return binding.root
    }

    private fun navigateToCameraScreen() {
       findNavController().navigate(R.id.action_homeFragment_to_camera)
    }

}