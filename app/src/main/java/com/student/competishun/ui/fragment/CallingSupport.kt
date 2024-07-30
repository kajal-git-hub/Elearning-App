package com.student.competishun.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.student.competishun.R

class CallingSupport : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.fragment_calling_support, null)

            val startCall = view.findViewById<ConstraintLayout>(R.id.clStartCall)
            startCall.setOnClickListener {
                //hold
            }

            builder.setView(view)
            val dialog = builder.create()

            // Set dialog window attributes
            dialog.window?.setGravity(Gravity.BOTTOM or Gravity.END)
            val params = dialog.window?.attributes
            params?.y = 150
            params?.x = 50
            dialog.window?.attributes = params

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}
