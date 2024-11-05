package xyz.penpencil.competishun.utils

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R

object DialogTestUtils {

    fun showOptionsDialog(context: Context, instructionCall: () -> Unit, reportCall: () -> Unit): Dialog? {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_instruction, null)
        dialog.setContentView(view)
        val instruction = view.findViewById<MaterialTextView>(R.id.instruction)
        val report = view.findViewById<MaterialTextView>(R.id.report)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        instruction.setOnClickListener {
            dialog.dismiss()
            instructionCall()
        }
        report.setOnClickListener {
            dialog.dismiss()
            reportCall()
        }
        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }

        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        return dialog
    }


    fun showReportDialog(context: Context, submitCall:(message: String, type: String)->Unit , reason:Boolean): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_report_test, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        val rbReport = view.findViewById<RadioGroup>(R.id.rbReport)

        val incorrectController = view.findViewById<RelativeLayout>(R.id.incorrectController)
        val missingController = view.findViewById<RelativeLayout>(R.id.missingController)
        val otherController = view.findViewById<RelativeLayout>(R.id.otherController)

        val etIncorrect = view.findViewById<TextInputEditText>(R.id.etIncorrect)
        val etMissing = view.findViewById<TextInputEditText>(R.id.etMissing)
        val etOther = view.findViewById<TextInputEditText>(R.id.etOther)

        val submit = view.findViewById<MaterialButton>(R.id.submit)

        val toVisible = view.findViewById<ConstraintLayout>(R.id.cl_otherReasonConstraint)

        if(reason){
            toVisible.visibility = View.VISIBLE
        }else{
            toVisible.visibility = View.GONE
        }

        val colorStateList = ColorStateList.valueOf(context.resources.getColor(R.color._808080, null))
        submit.backgroundTintList = colorStateList

        var type = ""

        val colorStateList1 = ColorStateList.valueOf(context.resources.getColor(R.color.PrimaryColor, null))
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        rbReport.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.incorrect -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    incorrectController.visibility = View.VISIBLE
                    missingController.visibility = View.GONE
                    otherController.visibility = View.GONE
                    type = "incorrect"
                }

                R.id.missing -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    incorrectController.visibility = View.GONE
                    missingController.visibility = View.VISIBLE
                    otherController.visibility = View.GONE
                    type = "missing"
                }

                R.id.other1 -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    incorrectController.visibility = View.GONE
                    missingController.visibility = View.GONE
                    otherController.visibility = View.VISIBLE
                    type = "other"
                }
            }
        }

        submit.setOnClickListener {
            dialog.dismiss()
            val message = when (type) {
                "incorrect" -> {
                    etIncorrect.text.toString()
                }
                "missing" -> {
                    etMissing.text.toString()
                }
                "other" -> {
                    etOther.text.toString()
                }
                else -> {
                    ""
                }
            }
            submitCall(message, type)
        }
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        return dialog
    }


    fun showReportSubmitDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_report_submit, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        val loader = view.findViewById<ImageView>(R.id.loader)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        Glide.with(view).load(R.drawable.loader).into(loader)
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        Handler(Looper.getMainLooper()).postDelayed({dialog.dismiss()}, 3000)
        return dialog
    }

    fun showInstructionDialog(context: Context): Dialog? {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_instruction_test, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        return dialog
    }


    fun showExitTestDialog(context: Context, exit:()->Unit): Dialog? {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_exit_test, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val exitDialog = view.findViewById<MaterialButton>(R.id.exitDialog)
        val exitBtn = view.findViewById<MaterialButton>(R.id.exitBtn)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        Glide.with(context)
            .load(R.drawable.ic_exit_test)
            .placeholder(R.drawable.ic_exit_test)
            .into(icon)
        exitDialog.setOnClickListener { dialog.dismiss()
            exit()}
        exitBtn.setOnClickListener { dialog.dismiss() }
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        return dialog
    }


    fun showSubjectDialog(context: Context, submitCall:(subject: String)->Unit): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_subject_test, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        val rbReport = view.findViewById<RadioGroup>(R.id.rbReport)

        val submit = view.findViewById<MaterialButton>(R.id.submit)
        val colorStateList = ColorStateList.valueOf(context.resources.getColor(R.color._808080, null))
        submit.backgroundTintList = colorStateList

        var type = ""

        val colorStateList1 = ColorStateList.valueOf(context.resources.getColor(R.color.PrimaryColor, null))
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        rbReport.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.maths -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    type = "Maths"
                }

                R.id.physics -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    type = "Physics"
                }

                R.id.chemistry -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    type = "Chemistry"
                }
            }
        }

        submit.setOnClickListener {
            dialog.dismiss()
            submitCall(type)
        }
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        return dialog
    }


    //TODO:DOWNLOAD FOR SCOREBoard
    fun showDownloadDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_download_option, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        Handler(Looper.getMainLooper()).postDelayed({dialog.dismiss()}, 3000)
        return dialog
    }


    //TODO:DOWNLOAD CONFIRMATION FOR SCOREBoard
    fun showDownloadConfirmationDialog(context: Context, course: String): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_download_confirmation_option, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        val mIcon = view.findViewById<ImageView>(R.id.icon)
        val header = view.findViewById<TextView>(R.id.header)
        Glide.with(dialog.context).load(R.drawable.ic_download_confirmation_animation).into(mIcon)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        header.text = Html.fromHtml("<b>\"$course\" </b>question paper is downloaded successfully", Html.FROM_HTML_MODE_COMPACT);

        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        Handler(Looper.getMainLooper()).postDelayed({dialog.dismiss()}, 3000)
        return dialog
    }

}