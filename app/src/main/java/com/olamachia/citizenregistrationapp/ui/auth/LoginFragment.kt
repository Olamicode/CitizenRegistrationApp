package com.olamachia.citizenregistrationapp.ui.auth

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.data.Hospital
import com.olamachia.citizenregistrationapp.databinding.FragmentLoginBinding
import com.olamachia.citizenregistrationapp.ui.utils.*
import com.olamachia.citizenregistrationapp.ui.viewmodels.CitizenViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var currentBinding: FragmentLoginBinding? = null
    private val binding get() = currentBinding!!

    private val viewModel: CitizenViewModel by viewModels()
    private lateinit var dialog: Dialog

    companion object {
        const val LOGIN_FRAGMENT_TAG = "LoginFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentBinding = FragmentLoginBinding.bind(view)
        dialog = showLoadingBar()

        binding.apply {
            toolbar.mainToolBar.setNavigationIcon(R.drawable.ic_left_arrow)
            toolbar.mainToolBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            validateFields()

            fragmentLoginLoginBtn.setOnClickListener {
                val email = fragmentLoginEmailEt.text.toString()
                val password = fragmentLoginHospitalPasswordEt.text.toString()

                dialog.show()
                viewModel.loginWithFirebaseAuth(requireContext(), email, password)
            }

            viewModel.loginLiveData.observe(viewLifecycleOwner, { details ->
                val hospital = details as Hospital
                if (hospital != null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.authentication_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    hospital.name?.let {
                        SessionManager.save(
                            requireContext(),
                            USERNAME,
                            it
                        )
                    }

                    val action = hospital.let {
                        LoginFragmentDirections
                            .actionLoginFragmentToDashboardFragment(it)
                    }

                    dialog.dismiss()
                    findNavController().navigate(action)

                } else {

                    dialog.dismiss()

                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }


    private fun validateFields() {
        val fields: MutableList<JDataClass> = mutableListOf(

            JDataClass(
                editText = binding.fragmentLoginEmailEt,
                editTextInputLayout = binding.fragmentLoginEmailTil,
                errorMessage = JDErrorConstants.INVALID_EMAIL_ERROR,
                validator = { it.jdValidateEmail(it.text.toString()) }
            ),

            JDataClass(
                editText = binding.fragmentLoginHospitalPasswordEt,
                editTextInputLayout = binding.fragmentLoginHospitalPasswordTil,
                errorMessage = JDErrorConstants.INVALID_PASSWORD_ERROR,
                validator = { it.jdValidatePassword(it.text.toString()) }
            ),


            )

        JDFormValidator.Builder()
            .addFieldsToValidate(fields)
            .removeErrorIcon(true)
            .viewsToEnable(mutableListOf(binding.fragmentLoginLoginBtn))
            .watchWhileTyping(true)
            .build()


    }

}