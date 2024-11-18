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


        binding.ivMapImage.setOnClickListener {
            openMapWithLocation(getString(R.string.competishun_location))
        }

        binding.ivFb.setOnClickListener {
            openFacebook()
        }
        binding.ivLinkedin.setOnClickListener {
            openLinkedIn()
        }
        binding.ivIg.setOnClickListener {
            openInstagram()
        }
        binding.ivWhatsUpp.setOnClickListener {
            openWhatsUp()
        }


        binding.etBTHomeAddress.setOnClickListener {
            findNavController().popBackStack()
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

    private fun openWhatsUp() {
        val instagramUrl = "https://www.whatsapp.com/channel/0029VarDkF2545uuken58c2Q?fbclid=PAY2xjawGapcZleHRuA2FlbQIxMQABpkXSl5K56tj5W4iWVte0AJOHWzmP7fBQK3ldSURpnrCTNIs3c-1JA8Zj-g_aem_I96S4QvYAAHkriUYL7Es_w"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
        startActivity(intent)
    }

    private fun openInstagram() {
        val instagramUrl = "https://www.instagram.com/competishun?igsh=MW9hYXRxMzRyb3Ztaw=="
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
        startActivity(intent)
    }

    private fun openFacebook() {
        val facebookUrl = "https://www.facebook.com/share/1QgPWvYaKv/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
        startActivity(intent)
    }

    private fun openLinkedIn() {
        val linkedInUrl = "https://in.linkedin.com/company/competishun"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl))
        startActivity(intent)
    }


    private fun openMapWithLocation(address: String) {
        val geoUri = Uri.parse("geo:0,0?q=$address")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

//    private fun openMapWithCoordinates(latitude: Double, longitude: Double) {
//        val geoUri = Uri.parse("geo:$latitude,$longitude")
//        val intent = Intent(Intent.ACTION_VIEW, geoUri)
//        intent.setPackage("com.google.android.apps.maps")
//
//        if (intent.resolveActivity(requireActivity().packageManager) != null) {
//            startActivity(intent)
//        }
//    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
    }


}