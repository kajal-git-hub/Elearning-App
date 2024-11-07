package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.DisclaimerItem
import xyz.penpencil.competishun.databinding.FragmentDisclaimerBinding
import xyz.penpencil.competishun.ui.adapter.DisclaimerAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity

class DisclaimerFragment : DrawerVisibility() {

    private lateinit var binding: FragmentDisclaimerBinding

    private lateinit var disclaimerAdapter: DisclaimerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDisclaimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val disclaimerList = listOf(
            DisclaimerItem(
                "What is the primary purpose of the content provided by this app ?",
                "The content provided by this app is intended solely for educational purposes to help students prepare for the IIT JEE and other competitive exams. While efforts are made to ensure accuracy and reliability, the information may not be completely error-free or up-to-date"
            ),
            DisclaimerItem(
                "Does the app guarantee specific academic or test results ?",
                "No, the app does not guarantee any specific academic or test results. Success in exams like the IIT JEE depends on various factors, including the student’s individual efforts, understanding, and dedication. The app provides resources, guidance, and support, but it cannot guarantee admission to any institution or a specific score."
            ),
            DisclaimerItem(
                "What is the app's stance on third-party links and content ?",
                "The app may include links or references to third-party content, websites, or resources. These are provided for convenience and informational purposes only. The app does not endorse, approve, or guarantee the accuracy of third-party content and is not responsible for any content or services provided by these third parties."
            ),
            DisclaimerItem(
                "Are study plans and recommendations provided in the app personalized ?",
                "No, all advice, tips, or study plans provided in the app are generalized recommendations and may not suit each student’s unique circumstances. Students are encouraged to seek additional guidance or support tailored to their personal learning needs and goals."
            ),
            DisclaimerItem(
                "What health and safety recommendations does the app provide ?",
                "The app recommends a balanced study routine, regular breaks, and healthy habits while using the app. Excessive screen time, continuous study sessions without rest, or lack of sleep may negatively impact health. Users are advised to consult appropriate professionals if they experience mental or physical health issues."
            ),
            DisclaimerItem(
                "What is the limitation of liability for the app ?",
                "Under no circumstances shall the app, its developers, or contributors be liable for any indirect, incidental, or consequential damages arising from using the app or relying on its content. Use of the app is entirely at the user’s own risk."
            ),
            DisclaimerItem(
                "What restrictions are in place regarding piracy ?",
                "Engaging in any form of piracy is strictly prohibited. Users found involved in piracy will have their access to the program revoked without refund and will be barred from future enrollments. Additionally, the app reserves the right to pursue lawsuits and criminal charges to the fullest extent of the law."
            ),
            DisclaimerItem(
                " What does a user agree to by using this app ?",
                "By using the app, the user acknowledges that they have read, understood, and agreed to the terms outlined in this disclaimer."
            ),
        )

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        binding.igTermsBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

//        faqItems = arguments?.getParcelableArrayList("faq_items") ?: emptyList()

        disclaimerAdapter = DisclaimerAdapter(disclaimerList)

        binding.rvDisclaimerPolicy.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = disclaimerAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }

    private fun handleBackPressed() {
        findNavController().navigate(R.id.homeFragment)
    }

}

