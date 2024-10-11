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
                "Refund & Cancelation Policies on Hire for Sure Plan",
                "India’s first career development platform that connects students to the right employers to democratize the job search process, in college and Colleges.\n" +
                        "This Terms &Conditions applies to your use of the Competishun  Service (or ‘the Service’) including the app.getwork.org website application, getwork.org website; as well as your relationship with Veerwal Competishun  Services Private Limited (“Competishun ”).\n" +
                        "\n" +
                        "If you do not agree with these Terms, including the Binding Arbitration Clause and a Class Action waiver included below, please discontinue using the Service.\n" +
                        "\n" +
                        "Our Terms &Conditions may change over time. Minor changes that do not change your rights will be reflected and modified in the Terms on our website. You will be notified via email or through the Competishun  service if we make alterations to these Terms that materially changes your rights. When you use the Competishun  Service after a modification is posted, you are telling us that you accept the modified terms.\n" +
                        "\n" +
                        "If you have a written agreement with Competishun  that states that it supersedes this Terms &Conditions (for example if you are a College or Employer Partner), then to the extent there is any conflict between the documents, the provisions in your separate agreement applies.\n" +
                        "\n" +
                        "Competishun  takes user privacy seriously and does not allow third parties from collecting student data, employer data, job descriptions, or other marketplace information from our Services through the use of automated scripts (“scraping”) or similar technologies or methodologies. For more information on our privacy practices, please read our Privacy Policy."
            ),
            DisclaimerItem(
                "Will Classes be any Live Classes ?",
                "NO, There will be only Recorded Scheduled Lectures will be provided. In week there will be LIVE INTERACTION Session for your guidance for 30 Minutes."
            ),
            DisclaimerItem(
                "Will Test be conducted in this course ?",
                "YES, Test will be conducted on weekly basis as per test grid that will be provided to you along with the course. Test will be conducted on COMPETISHUN DIGITAL APP / WEBSITE and we will share the complete details in your Official Support Prior to your 1st test"
            ),
            DisclaimerItem(
                "Will Doubt clearing session will be conducted ?",
                "YES, you can ask your doubts in your doubt groups tagging faculties and you will get a reply at the earliest."
            ),
            DisclaimerItem(
                "How Do you contact Support Staff of Competishun ?",
                "You can contact Support Staff at 8888-0000-21, 7410-900-901 "
            )
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

    private fun handleBackPressed() {
        findNavController().navigate(R.id.homeFragment)
    }

}

