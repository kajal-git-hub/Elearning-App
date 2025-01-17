package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentProfileBinding


@AndroidEntryPoint
class ProfileFragment : DrawerVisibility() {

    var TAG = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val userViewModel: UserViewModel by viewModels()
    private var studentClass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        studentClass = arguments?.getString("StudentClass","") ?: ""
//        Log.d("studentClass",studentClass)

        binding.igProfileCall.setOnClickListener {
            val phoneNumber = "8888000021"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
        binding.etProfileHelp.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.llMyDetails.setOnClickListener {
            findNavController().navigate(R.id.ProfileMyDetails)
        }

        binding.igEditProfile.setOnClickListener {
            val bottomSheetDescriptionFragment = ProfileEditFragment(){ year,course ->
                binding.tvYear.text =  year
                binding.tvExamType.text = "$course |"
            }
            bottomSheetDescriptionFragment.show(childFragmentManager, "BottomSheetDescriptionFragment")

        }

        observeUserDetails()
        userViewModel.fetchUserDetails()


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)


        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        binding.etBTUpload.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.llMyCart.setOnClickListener {
            findNavController().navigate(R.id.myCartFragment)
        }

        binding.llMyPurchase.setOnClickListener{
            findNavController().navigate(R.id.MyPurchase)
        }

        binding.llLogout.setOnClickListener {
            fragmentManager?.let {
                val bottomSheetDescriptionFragment = ProfileLogoutFragment()
                bottomSheetDescriptionFragment.show(it, "BottomSheetDescriptionFragment")
            }
        }
//        if(studentClass.isNotEmpty()){
//            binding.tvClass.visibility = View.VISIBLE
//            binding.tvClass.text = studentClass
//        }

        binding.ProfileUserName.text = sharedPreferencesManager.name
        if (!sharedPreferencesManager.email.isNullOrEmpty())
        binding.ProfileEmail.text = sharedPreferencesManager.email
        else binding.ProfileEmail.text = sharedPreferencesManager.mobileNo
        binding.tvExamType.text = sharedPreferencesManager.preparingFor
        binding.tvYear.text= "| "+sharedPreferencesManager.targetYear.toString()
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.d("userDetails",data.getMyDetails.fullName.toString())
                Log.d("userDetails",data.getMyDetails.userInformation.address?.city.toString())
                val name = data.getMyDetails.fullName
                val rollno = data.getMyDetails.userInformation.rollNumber
                if (!rollno.isNullOrEmpty()){
                    binding.ProfileRollNo.text = "Roll No:  $rollno"
                }
                if (!name.isNullOrEmpty()) {
                    binding.ProfileUserName.setText(name)
                }
//
            }.onFailure { exception ->
                Log.e(TAG,exception.message.toString())
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.hideCallingSupport()

    }


}