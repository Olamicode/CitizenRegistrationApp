package com.olamachia.citizenregistrationapp.ui.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.data.Countries
import com.olamachia.citizenregistrationapp.data.Hospital
import com.olamachia.citizenregistrationapp.data.StateJson
import com.olamachia.citizenregistrationapp.ui.dashboard.CitizenRegistrationFragment
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


fun Fragment.navigateToDestination(currentDestination: Int, destination: Int) {
    requireActivity().onBackPressedDispatcher.addCallback {

        if (findNavController().currentDestination?.id == currentDestination) {
            findNavController().navigate(destination)
        }

    }
}


fun Fragment.showAlertDialog(
    message: String,
    title: String,
    action: () -> Unit
) {
    val dialogBuilder = AlertDialog.Builder(requireContext())
    dialogBuilder.setMessage(message)
        .setPositiveButton("Yes") { dialog, which ->
            action()
        }
        .setNegativeButton("Cancel", null)

    val alert = dialogBuilder.create()
    alert.setTitle(title)
    alert.show()
}


@SuppressLint("ClickableViewAccessibility")
fun Fragment.pickAndSetDate(editText: TextInputEditText) {
    val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
    builder.setTitleText(getString(R.string.select_a_date))

    val picker: MaterialDatePicker<*> = builder.build()

    editText.setOnTouchListener { _, _ ->
        try {
            picker.show(parentFragmentManager, picker.toString())
        } catch (e: Exception) {
            Log.i(CitizenRegistrationFragment.CITIZEN_REGISTRATION, e.message.toString())
        }

        true
    }

    picker.addOnPositiveButtonClickListener {
        editText.setText(picker.headerText)
    }
}


fun Fragment.showLoadingBar(): Dialog {

    val dialog by lazy {
        Dialog(requireContext(), R.style.Theme_MaterialComponents_Dialog).apply {
            setContentView(R.layout.progress_bar_layout)
            setCanceledOnTouchOutside(false)
            setTitle("Verifying")
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    return dialog
}

fun Fragment.closeLoadingBar(dialog: Dialog) {
    dialog.dismiss()
}


fun Fragment.spannable(
    textView: TextView,
    start: Int,
    end: Int,
    foreGroundColor: Int,
    setUnderline: Boolean = false,
    onSpannableClick: (() -> Unit)? = null
) {

    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onSpannableClick?.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = foreGroundColor
            ds.isUnderlineText = setUnderline
        }
    }

    val spannable = SpannableStringBuilder(textView.text)
    spannable.setSpan(
        clickableSpan,
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    textView.text = spannable
    textView.movementMethod = LinkMovementMethod.getInstance()
}


fun Fragment.readJson(fileName: String): String? {
    var inputStream: InputStream? = null

    val jsonString: String

    try {
        inputStream = requireContext().assets.open(fileName)

        val size = inputStream.available()

        val buffer = ByteArray(size)

        inputStream.read(buffer)

        jsonString = String(buffer)

        return jsonString
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }

    return null
}


fun Fragment.provideStates(): MutableList<String> {
    val locations = Gson().fromJson(readJson("location.json"), StateJson::class.java)
    val states = mutableListOf<String>()

    locations.data.forEach { data ->
        states.add(data.state)
    }
    return states
}

fun Fragment.provideCountries(): MutableList<String> {
    val country = Gson().fromJson(readJson("countries.json"), Countries::class.java)
    val countries = mutableListOf<String>()

    country.data.forEach { data ->
        countries.add(data.name)
    }
    return countries
}

fun Fragment.provideGenders(): List<String> = listOf("Male", "Female", "Others")

fun Fragment.provideMartialStatuses(): List<String> = listOf("Single", "Married", "Divorced")

fun Fragment.providePlaceOfOccurrence(): List<String> = listOf(
    "Maternity Home", "Hospital", "At Home", "Tradition Doctor's Place", "Others"
)

fun Fragment.provideTypesOfBirth(): List<String> = listOf("Single", "Multiple")

fun Fragment.provideRelationships(): List<String> = listOf("Father", "Mother", "Others")

fun Fragment.provideDate(): String {
    val calendar = Calendar.getInstance().time
    val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    return simpleDateFormat.format(calendar)
}

fun provideHospital(context: Context): Hospital {

    return TypeConverter.convertStringToPojo(
        SessionManager.load(context, HOSPITAL_DATA)
    )
}

fun Fragment.validateFormEditTextFields(editTexts: List<TextInputEditText>): Boolean {

    for (editText in editTexts) {
        if (editText.text?.isEmpty() == true) {
            return false
        }
    }
    return true
}

fun Fragment.validateFormAutoCompleteTextViewFields(editTexts: List<AutoCompleteTextView>): Boolean {

    for (editText in editTexts) {
        if (editText.text?.isEmpty() == true) {
            return false
        }
    }

    return true
}