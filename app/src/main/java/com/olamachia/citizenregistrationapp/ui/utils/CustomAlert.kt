package com.olamachia.citizenregistrationapp.ui.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.olamachia.citizenregistrationapp.R

object CustomAlert {

    fun showDialog (context: Context,
                    titleToDisplay : String? = null,
                    descriptionToDisplay : String? = null,
                    layoutRes: Int,
                    titleTextView: TextView? = null,
                    descriptionTextView: TextView? = null,
                    buttonID: Int,
                    action: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(layoutRes)

        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (titleTextView != null) {
            titleTextView.text = titleToDisplay.toString()
        }
        if (descriptionTextView != null) {
            descriptionTextView.text = descriptionToDisplay.toString()
        }

        val dialogButton : Button = dialog.findViewById(buttonID)

        dialogButton.isClickable = true

        dialogButton.setOnClickListener {
            action()
            dialog.dismiss()
        }
        dialog.show()

    }
}