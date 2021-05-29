package com.olamachia.citizenregistrationapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.databinding.FragmentLandingBinding

class LandingFragment : Fragment(R.layout.fragment_landing) {

    private var currentBinding: FragmentLandingBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentBinding = FragmentLandingBinding.bind(view)

        binding.apply {
            fragmentLandingCreateAccountBtn.setOnClickListener {
                findNavController().navigate(R.id.registerFragment)
            }
            fragmentLandingLoginBtn.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }

}