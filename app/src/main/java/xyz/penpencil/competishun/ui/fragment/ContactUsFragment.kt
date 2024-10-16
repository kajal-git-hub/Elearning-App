package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentContactUsBinding
import xyz.penpencil.competishun.ui.main.HomeActivity

class ContactUsFragment : DrawerVisibility() {


    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etBTHomeAddress.setOnClickListener {
            findNavController().navigate(R.id.action_ContactUs_to_homePage)
        }

        binding.tvCallonNo.setOnClickListener {
            val phoneNumber = "8888000021"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        binding.tvEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@competishun.com")
            }

            if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(emailIntent)
            } else {
                Toast.makeText(requireContext(), "No email client installed.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }


}