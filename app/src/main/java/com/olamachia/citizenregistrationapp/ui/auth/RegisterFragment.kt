package com.olamachia.citizenregistrationapp.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.data.Hospital
import com.olamachia.citizenregistrationapp.databinding.FragmentRegisterBinding
import com.olamachia.citizenregistrationapp.ui.utils.*
import com.olamachia.citizenregistrationapp.ui.viewmodels.CitizenViewModel
import java.util.*


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var currentBinding: FragmentRegisterBinding? = null
    private val binding get() = currentBinding!!
    private lateinit var dialog: Dialog

    private val viewModel: CitizenViewModel by viewModels()

    companion object {
        const val REGISTER_FRAGMENT_TAG = "RegisterFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentRegisterBinding.bind(view)
        dialog = showLoadingBar()


        binding.apply {
            toolbar.mainToolBar.setNavigationIcon(R.drawable.ic_left_arrow)
            toolbar.mainToolBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            /** validating fields */
            validateFields()

            spannable(login, 25, 31, R.color.primary_blue_100, false) {
                findNavController().navigate(R.id.loginFragment)
            }

            fragmentLandingCreateAccountBtn.setOnClickListener {
                val name = fragmentRegisterHospitalNameEt.text.toString()
                val email = fragmentRegisterEmailEt.text.toString()
                val password = fragmentRegisterHospitalPasswordEt.text.toString()

                dialog.show()
                viewModel.authenticateWithFirebase(requireActivity(), name, email, password)
            }

            viewModel.authLiveData.observe(viewLifecycleOwner, {
                if (it) {
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_success),
                        Toast.LENGTH_SHORT
                    ).show()
                  dialog.dismiss()
                  findNavController().navigate(R.id.loginFragment)
                }
            })

        }
    }


    private fun validateFields() {
        val fields: MutableList<JDataClass> = mutableListOf(

            JDataClass(
                editText = binding.fragmentRegisterHospitalNameEt,
                editTextInputLayout = binding.fragmentRegisterHospitalNameTil,
                errorMessage = JDErrorConstants.NAME_FIELD_ERROR,
                validator = { it.jdValidateName(it.text.toString()) }
            ),

            JDataClass(
                editText = binding.fragmentRegisterEmailEt,
                editTextInputLayout = binding.fragmentRegisterEmailTil,
                errorMessage = JDErrorConstants.INVALID_EMAIL_ERROR,
                validator = { it.jdValidateEmail(it.text.toString()) }
            ),

            JDataClass(
                editText = binding.fragmentRegisterHospitalPasswordEt,
                editTextInputLayout = binding.fragmentRegisterHospitalPasswordTil,
                errorMessage = JDErrorConstants.INVALID_PASSWORD_ERROR,
                validator = { it.jdValidatePassword(it.text.toString()) }
            ),

            JDataClass(
                editText = binding.fragmentRegisterConfirmHospitalPasswordEt,
                editTextInputLayout = binding.fragmentRegisterConfirmHospitalPasswordTil,
                errorMessage = JDErrorConstants.PASSWORD_DOES_NOT_MATCH,
                validator = {
                    it.jdValidateConfirmPassword(
                        binding.fragmentRegisterHospitalPasswordEt,
                        binding.fragmentRegisterConfirmHospitalPasswordEt
                    )
                }
            ),


            )

        JDFormValidator.Builder()
            .addFieldsToValidate(fields)
            .removeErrorIcon(true)
            .viewsToEnable(mutableListOf(binding.fragmentLandingCreateAccountBtn))
            .watchWhileTyping(true)
            .build()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }

}