package com.olamachia.citizenregistrationapp.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.data.*
import com.olamachia.citizenregistrationapp.databinding.FragmentCitizenRegistrationBinding
import com.olamachia.citizenregistrationapp.ui.utils.*
import com.olamachia.citizenregistrationapp.ui.viewmodels.CitizenViewModel
import java.text.SimpleDateFormat
import java.util.*


class CitizenRegistrationFragment : Fragment(R.layout.fragment_citizen_registration) {

    private var currentBinding: FragmentCitizenRegistrationBinding? = null
    private val binding get() = currentBinding!!
    private var stepCount = 1
    private lateinit var child: Child
    private lateinit var mother: Parent
    private lateinit var father: Parent
    private lateinit var informant: Informant
    private lateinit var citizen: Citizen
    private lateinit var dialog: Dialog

    private val viewModel: CitizenViewModel by viewModels()

    companion object {
        const val CITIZEN_REGISTRATION = "CitizenRegistration"
        const val ONE = 1
        const val TWO = 2
        const val THREE = 3
        const val FOUR = 4
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentCitizenRegistrationBinding.bind(view)

        dialog = showLoadingBar()

        binding.apply {
            toolbar.mainToolBar.setNavigationIcon(R.drawable.ic_left_arrow)
            toolbar.mainToolBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            pickAndSetDate(particularOfChildLy.fragmentCitizenRegisterDateOfBirthEt)

            val genders = provideGenders()
            val martialStatuses = provideMartialStatuses()
            val placeOfOccurrence = providePlaceOfOccurrence()
            val typesOfBirth = provideTypesOfBirth()
            val relationships = provideRelationships()
            val states = provideStates()
            val countries = provideCountries()

            val particularOfChildLyList = listOf(
                particularOfChildLy.fragmentCitizenRegisterFirstNameEt,
                particularOfChildLy.fragmentCitizenRegisterLastNameEt,
                particularOfChildLy.fragmentCitizenRegisterTownEt,
                particularOfChildLy.fragmentCitizenRegisterBirthOrderEt
            )

            val particularOfChildLyAutoCompleteTextViews = listOf(
                particularOfChildLy.fragmentCitizenRegisterGenderEt,
                particularOfChildLy.fragmentCitizenRegisterAddressEt,
                particularOfChildLy.fragmentCitizenRegisterBirthTypeEt,
            )

            val particularOfMotherLyList = listOf(
                particularOfMotherLy.fragmentCitizenRegisterFirstNameEt,
                particularOfMotherLy.fragmentCitizenRegisterLastNameEt,
                particularOfMotherLy.fragmentCitizenRegisterEthnicityEt,
                particularOfMotherLy.fragmentCitizenRegisterEducationEt,
                particularOfMotherLy.fragmentCitizenRegisterOccupationEt,
                particularOfMotherLy.fragmentCitizenRegisterPhoneEt,
                particularOfMotherLy.fragmentCitizenRegisterNationalNoEt
            )

            val particularOfMotherLyAutoCompleteTextView = listOf(
                particularOfMotherLy.fragmentCitizenRegisterMartialStatusEt,
                particularOfMotherLy.fragmentCitizenRegisterNationalityEt,
                particularOfMotherLy.fragmentCitizenRegisterStateEt,
            )

            val particularOfFatherLyList = listOf(
                particularOfFatherLy.fragmentCitizenRegisterFirstNameEt,
                particularOfFatherLy.fragmentCitizenRegisterLastNameEt,
                particularOfFatherLy.fragmentCitizenRegisterEthnicityEt,
                particularOfFatherLy.fragmentCitizenRegisterEducationEt,
                particularOfFatherLy.fragmentCitizenRegisterOccupationEt,
                particularOfFatherLy.fragmentCitizenRegisterPhoneEt,
                particularOfFatherLy.fragmentCitizenRegisterNationalNoEt
            )

            val particularOfFatherLyAutoCompleteTextView = listOf(
                particularOfFatherLy.fragmentCitizenRegisterMartialStatusEt,
                particularOfFatherLy.fragmentCitizenRegisterNationalityEt,
                particularOfFatherLy.fragmentCitizenRegisterStateEt,
            )

            val particularOfInformantLyList = listOf(
                particularOfInformantLy.fragmentCitizenRegisterFirstNameEt,
                particularOfInformantLy.fragmentCitizenRegisterLastNameEt,
                particularOfInformantLy.fragmentCitizenRegisterAddressEt,
                particularOfInformantLy.fragmentCitizenRegisterPhoneEt,
                particularOfInformantLy.fragmentCitizenRegisterNationalNoEt
            )

            val particularOfInformantLyAutoCompleteTextView = listOf(
                particularOfInformantLy.fragmentCitizenRegisterRelationshipEt
            )

            val genderAdapter = ArrayAdapter(requireContext(), R.layout.list_item, genders)
            (particularOfChildLy.fragmentCitizenRegisterGenderTil.editText as?
                    AutoCompleteTextView)?.setAdapter(genderAdapter)

            val typesOfBirthAdapter =
                ArrayAdapter(requireContext(), R.layout.list_item, typesOfBirth)
            (particularOfChildLy.fragmentCitizenRegisterBirthTypeTil.editText as?
                    AutoCompleteTextView)?.setAdapter(typesOfBirthAdapter)

            val relationshipsAdapter =
                ArrayAdapter(requireContext(), R.layout.list_item, relationships)
            (particularOfInformantLy.fragmentCitizenRegisterRelationshipTil.editText as?
                    AutoCompleteTextView)?.setAdapter(relationshipsAdapter)


            val placeOfOccurrenceAdapter =
                ArrayAdapter(requireContext(), R.layout.list_item, placeOfOccurrence)
            (particularOfChildLy.fragmentCitizenRegisterAddressTil.editText as?
                    AutoCompleteTextView)?.setAdapter(placeOfOccurrenceAdapter)


            val placeOfOccurrenceEditTextListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val placePicked = parent.getItemAtPosition(position).toString()
                    particularOfChildLy.fragmentCitizenRegisterSpecifyTil.isVisible =
                        placePicked == placeOfOccurrence[placeOfOccurrence.size - 1]
                }

            (particularOfChildLy.fragmentCitizenRegisterAddressTil.editText as?
                    AutoCompleteTextView)?.onItemClickListener = placeOfOccurrenceEditTextListener


            val relationshipEditTextListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val relationshipPicked = parent.getItemAtPosition(position).toString()
                    particularOfInformantLy.fragmentCitizenRegisterSpecifyTil.isVisible =
                        relationshipPicked == relationships[relationships.size - 1]
                }

            (particularOfInformantLy.fragmentCitizenRegisterRelationshipTil.editText as?
                    AutoCompleteTextView)?.onItemClickListener = relationshipEditTextListener


            val martialStatusesAdapter =
                ArrayAdapter(requireContext(), R.layout.list_item, martialStatuses)
            (particularOfMotherLy.fragmentCitizenRegisterMartialStatusTil.editText as?
                    AutoCompleteTextView)?.setAdapter(martialStatusesAdapter)
            (particularOfFatherLy.fragmentCitizenRegisterMartialStatusTil.editText as?
                    AutoCompleteTextView)?.setAdapter(martialStatusesAdapter)


            val statesAdapter = ArrayAdapter(requireContext(), R.layout.list_item, states)
            (particularOfMotherLy.fragmentCitizenRegisterStateTil.editText as?
                    AutoCompleteTextView)?.setAdapter(statesAdapter)
            (particularOfFatherLy.fragmentCitizenRegisterStateTil.editText as?
                    AutoCompleteTextView)?.setAdapter(statesAdapter)

            val countriesAdapter = ArrayAdapter(requireContext(), R.layout.list_item, countries)
            (particularOfMotherLy.fragmentCitizenRegisterNationalityTil.editText as?
                    AutoCompleteTextView)?.setAdapter(countriesAdapter)
            (particularOfFatherLy.fragmentCitizenRegisterNationalityTil.editText as?
                    AutoCompleteTextView)?.setAdapter(countriesAdapter)


            continueBtn.setOnClickListener {

                when (fragmentCitizenRegisterStepOneTv.text.toString()) {
                    getString(R.string.step_1) -> {

                        if (validateFormEditTextFields(particularOfChildLyList) &&
                            validateFormAutoCompleteTextViewFields(
                                particularOfChildLyAutoCompleteTextViews
                            )
                        ) {

                            particularOfChildLy.apply {
                                val firstname = fragmentCitizenRegisterFirstNameEt.text.toString()
                                val lastname = fragmentCitizenRegisterLastNameEt.text.toString()
                                val dateOfBirth =
                                    fragmentCitizenRegisterDateOfBirthEt.text.toString()
                                val gender = fragmentCitizenRegisterGenderEt.text.toString()
                                val placeOfOccurrence =
                                    fragmentCitizenRegisterAddressEt.text.toString()
                                val town = fragmentCitizenRegisterTownEt.text.toString()
                                val typeOfBirth = fragmentCitizenRegisterBirthTypeEt.text.toString()
                                val birthOrder = fragmentCitizenRegisterBirthOrderEt.text.toString()
                                child = Child(
                                    firstname,
                                    lastname,
                                    dateOfBirth,
                                    gender,
                                    placeOfOccurrence,
                                    town,
                                    typeOfBirth,
                                    birthOrder

                                )
                            }


                            stepCount = TWO
                            fragmentCitizenRegisterStepOneTv.text = getString(R.string.step_two)
                            fragmentCitizenRegisterTitleTv.text =
                                getString(R.string.particulars_of_mother)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "All fields are required *.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    getString(R.string.step_two) -> {

                        if (validateFormEditTextFields(particularOfMotherLyList) &&
                            validateFormAutoCompleteTextViewFields(
                                particularOfMotherLyAutoCompleteTextView
                            )
                        ) {

                            particularOfMotherLy.apply {
                                val firstname = fragmentCitizenRegisterFirstNameEt.text.toString()
                                val lastname = fragmentCitizenRegisterLastNameEt.text.toString()
                                val martialStatus =
                                    fragmentCitizenRegisterMartialStatusEt.text.toString()
                                val nationality =
                                    fragmentCitizenRegisterNationalityEt.text.toString()
                                val stateOfOrigin = fragmentCitizenRegisterStateEt.text.toString()
                                val ethnicOrigin =
                                    fragmentCitizenRegisterEthnicityEt.text.toString()
                                val educationLevel =
                                    fragmentCitizenRegisterEducationEt.text.toString()
                                val occupation = fragmentCitizenRegisterOccupationEt.text.toString()
                                val phoneNumber = fragmentCitizenRegisterPhoneEt.text.toString()
                                val nationalNoID =
                                    fragmentCitizenRegisterNationalNoEt.text.toString()

                                mother = Parent(
                                    firstname,
                                    lastname,
                                    martialStatus,
                                    nationality,
                                    stateOfOrigin,
                                    ethnicOrigin,
                                    educationLevel,
                                    occupation,
                                    phoneNumber,
                                    nationalNoID
                                )

                            }

                            stepCount = THREE
                            fragmentCitizenRegisterStepOneTv.text = getString(R.string.step_three)
                            fragmentCitizenRegisterTitleTv.text =
                                getString(R.string.particulars_of_father)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "All fields are required *.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    getString(R.string.step_three) -> {

                        if (validateFormEditTextFields(particularOfFatherLyList) &&
                            validateFormAutoCompleteTextViewFields(
                                particularOfFatherLyAutoCompleteTextView
                            )
                        ) {

                            particularOfFatherLy.apply {
                                val firstname = fragmentCitizenRegisterFirstNameEt.text.toString()
                                val lastname = fragmentCitizenRegisterLastNameEt.text.toString()
                                val martialStatus =
                                    fragmentCitizenRegisterMartialStatusEt.text.toString()
                                val nationality =
                                    fragmentCitizenRegisterNationalityEt.text.toString()
                                val stateOfOrigin = fragmentCitizenRegisterStateEt.text.toString()
                                val ethnicOrigin =
                                    fragmentCitizenRegisterEthnicityEt.text.toString()
                                val educationLevel =
                                    fragmentCitizenRegisterEducationEt.text.toString()
                                val occupation = fragmentCitizenRegisterOccupationEt.text.toString()
                                val phoneNumber = fragmentCitizenRegisterPhoneEt.text.toString()
                                val nationalNoID =
                                    fragmentCitizenRegisterNationalNoEt.text.toString()

                                father = Parent(
                                    firstname,
                                    lastname,
                                    martialStatus,
                                    nationality,
                                    stateOfOrigin,
                                    ethnicOrigin,
                                    educationLevel,
                                    occupation,
                                    phoneNumber,
                                    nationalNoID
                                )

                            }


                            stepCount = FOUR
                            fragmentCitizenRegisterStepOneTv.text = getString(R.string.step_four)
                            fragmentCitizenRegisterTitleTv.text =
                                getString(R.string.particulars_of_informant)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "All fields are required *.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    else -> {

                        if (validateFormEditTextFields(particularOfInformantLyList) &&
                            validateFormAutoCompleteTextViewFields(
                                particularOfInformantLyAutoCompleteTextView
                            )
                        ) {

                            particularOfInformantLy.apply {
                                val firstname = fragmentCitizenRegisterFirstNameEt.text.toString()
                                val lastname = fragmentCitizenRegisterLastNameEt.text.toString()
                                val relationship =
                                    fragmentCitizenRegisterRelationshipEt.text.toString()
                                val address = fragmentCitizenRegisterAddressEt.text.toString()
                                val phoneNumber = fragmentCitizenRegisterPhoneEt.text.toString()
                                val nationalIDNo =
                                    fragmentCitizenRegisterNationalNoEt.text.toString()

                                informant = Informant(
                                    firstname,
                                    lastname,
                                    relationship,
                                    address,
                                    phoneNumber,
                                    nationalIDNo
                                )
                            }

                            val date = provideDate()

                            val hospital = provideHospital(requireContext())

                            citizen = Citizen(
                                child, mother, father, informant, date, "Pending", hospital
                            )

                            showAlertDialog(
                                message = getString(R.string.submit_question),
                                title = getString(R.string.submit_str),
                                action = {

                                    /** Saving to firebase */
                                    viewModel.saveCitizenDetailsToFirebase(
                                        requireContext(),
                                        citizen
                                    )

                                    dialog.show()

                                })

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "All fields are required *.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }

                }

                setViewsVisibleOrHidden()
            }

            previousBtn.setOnClickListener {
                when (fragmentCitizenRegisterStepOneTv.text.toString()) {
                    getString(R.string.step_four) -> {
                        stepCount = THREE
                        fragmentCitizenRegisterStepOneTv.text = getString(R.string.step_three)
                        fragmentCitizenRegisterTitleTv.text =
                            getString(R.string.particulars_of_father)
                    }
                    getString(R.string.step_three) -> {
                        stepCount = TWO
                        fragmentCitizenRegisterStepOneTv.text = getString(R.string.step_two)
                        fragmentCitizenRegisterTitleTv.text =
                            getString(R.string.particulars_of_mother)
                    }

                    getString(R.string.step_two) -> {
                        stepCount = ONE
                        fragmentCitizenRegisterStepOneTv.text = getString(R.string.step_1)
                        fragmentCitizenRegisterTitleTv.text =
                            getString(R.string.particulars_of_child_str)
                    }

                }
                setViewsVisibleOrHidden()
            }

            viewModel.citizenDetailsLiveData.observe(viewLifecycleOwner, {
                if (it) {
                    dialog.dismiss()

                    CustomAlert.showDialog(
                        context = requireContext(),
                        layoutRes = R.layout.submission_dialog,
                        buttonID = R.id.success_dialog_close_btn,
                        action = {
                            findNavController().navigate(R.id.dashboardFragment)
                        }
                    )

                } else {
                    dialog.dismiss()
                    Snackbar.make(
                        requireView(),
                        getString(R.string.error_submitting),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            })

        }

    }


    private fun setViewsVisibleOrHidden() {
        binding.apply {
            particularOfChildLy.childLy.isVisible =
                fragmentCitizenRegisterStepOneTv.text.toString() == getString(R.string.step_1) &&
                        stepCount == ONE

            particularOfMotherLy.motherLy.isVisible =
                fragmentCitizenRegisterStepOneTv.text.toString() == getString(R.string.step_two) &&
                        stepCount == TWO

            particularOfFatherLy.fatherLy.isVisible =
                fragmentCitizenRegisterStepOneTv.text.toString() == getString(R.string.step_three) &&
                        stepCount == THREE


            particularOfInformantLy.informantLy.isVisible =
                fragmentCitizenRegisterStepOneTv.text.toString() == getString(R.string.step_four) &&
                        stepCount == FOUR

            previousBtn.isVisible = stepCount != ONE

            continueIv.setImageResource(
                when {
                    fragmentCitizenRegisterStepOneTv.text.toString() ==
                            getString(R.string.step_four) -> R.drawable.ic_check
                    else -> R.drawable.ic_arrow
                }
            )

            nestedSrollView.scrollTo(0, 0)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }

}