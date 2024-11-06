package xyz.penpencil.competishun.ui.fragment.test

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import xyz.penpencil.competishun.utils.DialogTestUtils
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentTestBinding


class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private var optionsDialog: Dialog?=null
    private var showReportDialog: Dialog?=null
    private var showExitTestDialog: Dialog?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTestStatus("+1", "0")
        answerSelection()
        setAnsOptions("a2 – b2 + 2ac = 0", "A", binding.awsOne)
        setAnsOptions("a2 – b2 + 2ac = 0", "B", binding.awsTwo)
        setAnsOptions("a2 + c2 + 2ab = 0", "C", binding.awsThree)
        setAnsOptions("a2 – b2 – 2ac = 0", "D ", binding.awsFour)

        clickListener()
        changeSubmitButtonView()
        backPress()
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
        binding.subject.setOnClickListener {
            showSubjectSection()
        }

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
            it.findNavController().navigate(R.id.action_testFragment_to_testSplashFragment, Bundle().apply {
                putString("MESSAGE", "Submitting your test...")
                putBoolean("IS_TEST_START", false)
            })
        }

        binding.viewSolution.setOnClickListener {
            it.findNavController().navigate(R.id.action_testFragment_to_viewSolutionFragment,Bundle().apply {
                putString("QUESTION_ID", "Q6765")
            })
        }

        binding.options.setOnClickListener {
            optionsDialog = DialogTestUtils.showOptionsDialog(requireContext(), {
                showInstructionSection()
            },{
                showReportSection()
            })
            optionsDialog?.show()
        }

        binding.testResult.setOnClickListener {
            fragmentManager?.let { TestStatusFragment().show(it, "test") }
        }
    }

    private fun showInstructionSection(){
        val showInstructionDialog = DialogTestUtils.showInstructionDialog(requireContext())
        showInstructionDialog?.show()
    }

    private fun showReportSection(){
     /*   showReportDialog = DialogTestUtils.showReportDialog(requireContext()) { message: String, type: String ->
            showReportSubmitSection()
        }*/
        showReportDialog?.show()
    }

    private fun showReportSubmitSection(){
        val showReportSubmitDialog = DialogTestUtils.showReportSubmitDialog(requireContext())
        showReportSubmitDialog.show()
    }

    private fun showSubjectSection(){
        val showSubjectDialog = DialogTestUtils.showSubjectDialog(requireContext()){
            binding.subject.text = it
        }
        showSubjectDialog.show()
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

    private fun backPress(){
        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    v?.let {
                        showExitTestDialog = DialogTestUtils.showExitTestDialog(it.context) {

                        }
                        if (showExitTestDialog?.isShowing != true) {
                            showExitTestDialog?.show()
                        }
                    }
                    return true
                }
                return false
            }
        })
    }
    
}