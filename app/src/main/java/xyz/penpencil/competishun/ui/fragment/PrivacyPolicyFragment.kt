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
import xyz.penpencil.competishun.data.model.PolicyItem
import xyz.penpencil.competishun.databinding.FragmentPrivacyPolicyBinding
import xyz.penpencil.competishun.ui.adapter.PolicyAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity

class PrivacyPolicyFragment : Fragment() {

    private lateinit var binding: FragmentPrivacyPolicyBinding
    private lateinit var policyAdapter: PolicyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val policyList = listOf(
            PolicyItem(
                "What Personal Information We collect from You ?",
                "We collect certain information about You to help us serve You better. The information collected by Us is of the following nature such as Name, Telephone Number, Email Address, Your IP address, Information about your device, Network information, College / Institution details and location, User uploaded photo and IDs, Demographic information such as postcode, preferences and interests and other relevant information."
            ),
            PolicyItem(
                "What is the Use case  of Personal Information by Competishun?",
                "The information collected by Us through our Website /app is used by Us for various purposes to enable us to serve you better like, To find third party service providers, Internal record keeping, We may use the information to improve our products and services and other relevant interest."
            ),
            PolicyItem(
                "Will Competishun Share Personal Information ?",
                " We may share your information with payment service providers, regulatory authorities, and third-party agencies in the event of any request from such authorities."
            ),
            PolicyItem(
                "Is My Information secured with Competishun?",
                "All information is saved and stored on servers which are secured with passwords and pins to ensure no unauthorised person has access to it. Once your information is in our possession we adhere to strict security guidelines, protecting it against unauthorized access."
            ),
        )

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        binding.tvTitle.setOnClickListener {
            findNavController().popBackStack()
        }

//        faqItems = arguments?.getParcelableArrayList("faq_items") ?: emptyList()

        policyAdapter = PolicyAdapter(policyList)

        binding.rvAllPolicy.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = policyAdapter
        }

    }
    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }

    private fun handleBackPressed() {
        findNavController().popBackStack()
    }

}

