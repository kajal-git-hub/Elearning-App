package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentContactUsBinding
import xyz.penpencil.competishun.ui.main.HomeActivity

class ContactUsFragment : Fragment() {


    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etBTHomeAddress.setOnClickListener {
            findNavController().navigate(R.id.action_ContactUs_to_homePage)
        }
    }
    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }


}