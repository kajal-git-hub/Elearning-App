package com.student.competishun.ui.fragment.test

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R
import com.student.competishun.databinding.FragmentTestBinding


class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testSubjectSpinner()
        setTestStatus("+1", "0")
        answerSelection()
        setAnsOptions("a2 – b2 + 2ac = 0", "A", binding.awsOne)
        setAnsOptions("a2 – b2 + 2ac = 0", "B", binding.awsTwo)
        setAnsOptions("a2 + c2 + 2ab = 0", "C", binding.awsThree)
        setAnsOptions("a2 – b2 – 2ac = 0", "D ", binding.awsFour)

        clickListener()
        changeSubmitButtonView()
    }

    private fun changeSubmitButtonView() {
        if (binding.submit.isEnabled) {
            val colorStateList = ColorStateList.valueOf(resources.getColor(R.color.PrimaryColor, null))
            binding.submit.backgroundTintList = colorStateList
        } else {
            val colorStateList = ColorStateList.valueOf(resources.getColor(R.color._808080, null))
            binding.submit.backgroundTintList = colorStateList
        }
    }

    private fun setTestStatus(correct: String, wrong: String){
        val text = "<font color=#000000>Marks:  </font><font color=#357F2F>$correct</font> <font color=#E25B53>$wrong</font>"
        binding.testStatus.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun testSubjectSpinner(){
        val planetsArray = arrayOf("Maths", "Chemistry", "Physics", "Biology")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, planetsArray)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subject.adapter = adapter
        binding.subject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setAnsOptions(ans:String, option: String, radioButton: RadioButton){
        val text = "<font color=#F2A779>$option </font><font color=#2B2829> $ans</font>"
        radioButton.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun answerSelection(){
        binding.awsOne.setOnClickListener {
            selectRadioButton(selected = binding.awsOne)
        }
        binding.awsTwo.setOnClickListener {
            selectRadioButton(selected = binding.awsTwo)
        }
        binding.awsThree.setOnClickListener {
            selectRadioButton(selected = binding.awsThree)
        }
        binding.awsFour.setOnClickListener {
            selectRadioButton(selected = binding.awsFour)
        }
    }

    private fun selectRadioButton(selected: RadioButton) {
        selected.setBackgroundResource(R.drawable.rounded_test_select)
        val radioButtons = listOf(binding.awsOne, binding.awsTwo,
            binding.awsThree, binding.awsFour)
        for (radioButton in radioButtons) {
            if (radioButton == selected) {
                radioButton.isChecked = true
            } else {
                radioButton.isChecked = false
                radioButton.background = null
            }
        }
        binding.submit.isEnabled = true
        changeSubmitButtonView()
    }

    private fun clickListener(){
        binding.submit.setOnClickListener {
            binding.submit.isEnabled = false
            disableAns(false)
            binding.submit.visibility = View.GONE
            binding.nextQuestionController.visibility = View.VISIBLE
            checkCorrectAnswer(binding.awsOne, binding.awsFour)

        }

        binding.nextQuestion.setOnClickListener {
            //Write logic for next question
            disableAns(true)
            binding.submit.isEnabled = false
            changeSubmitButtonView()
        }

        binding.viewSolution.setOnClickListener {
            it.findNavController().navigate(R.id.action_testFragment_to_viewSolutionFragment,Bundle().apply {
                putString("QUESTION_ID", "Q6765")
            })
        }

        binding.options.setOnClickListener { showInstructionDialog() }
    }

    private fun disableAns(isEnable: Boolean){
        val radioButtons = listOf(binding.awsOne, binding.awsTwo,
            binding.awsThree, binding.awsFour)
        for (radioButton in radioButtons) {
            radioButton.isEnabled = isEnable
        }
    }

    private fun checkCorrectAnswer(correctAns: RadioButton, selectedAns: RadioButton) {
        val radioButtons = listOf(binding.awsOne, binding.awsTwo,
            binding.awsThree, binding.awsFour)
        for (radioButton in radioButtons) {
            if (correctAns == selectedAns){
                radioButton.isChecked = true
                radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.selector_test_submit, 0, 0, 0)
            }else {
                correctAns.isChecked = true
                selectedAns.isChecked = true
                correctAns.setBackgroundResource(R.drawable.selector_correct_bg_ans)
                selectedAns.setBackgroundResource(R.drawable.selector_wrong_bg_ans)
                correctAns.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.selector_correct_ans, 0, 0, 0)
                selectedAns.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.selector_wrong_ans, 0, 0, 0)
                if (radioButton != correctAns && radioButton!=selectedAns){
                    radioButton.background = null
                    radioButton.isChecked = false
                }
            }
        }
    }


    private fun showInstructionDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_instruction, null)
        dialog.setContentView(view)
        val instruction = view.findViewById<MaterialTextView>(R.id.instruction)
        val report = view.findViewById<MaterialTextView>(R.id.report)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        instruction.setOnClickListener { dialog.dismiss() }
        report.setOnClickListener {
            dialog.dismiss()
            showReportDialog()
        }
        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }

        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        dialog.show()
    }


    private fun showReportDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_report_test, null)
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
        val colorStateList = ColorStateList.valueOf(resources.getColor(R.color._808080, null))
        submit.backgroundTintList = colorStateList

        val colorStateList1 = ColorStateList.valueOf(resources.getColor(R.color.PrimaryColor, null))
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
                }

                R.id.missing -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    incorrectController.visibility = View.GONE
                    missingController.visibility = View.VISIBLE
                    otherController.visibility = View.GONE
                }

                R.id.other1 -> {
                    submit.isEnabled = true
                    submit.backgroundTintList = colorStateList1
                    incorrectController.visibility = View.GONE
                    missingController.visibility = View.GONE
                    otherController.visibility = View.VISIBLE
                }
            }
        }

        submit.setOnClickListener {
            dialog.dismiss()
            showReportSubmitDialog()
        }

        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        dialog.show()
    }


    private fun showReportSubmitDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_report_submit, null)
        dialog.setContentView(view)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val mHeader = view.findViewById<RelativeLayout>(R.id.mHeader)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        root.setOnClickListener { dialog.dismiss() }
        mHeader.setOnClickListener { }

        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels).toInt()
        dialog.window?.attributes = layoutParams
        Handler(Looper.getMainLooper()).postDelayed({dialog.dismiss()}, 3000)
        dialog.show()
    }
}