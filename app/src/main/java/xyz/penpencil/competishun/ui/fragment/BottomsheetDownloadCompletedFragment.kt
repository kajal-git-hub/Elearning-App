package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentBottomsheetDownloadCompletedBinding

class BottomsheetDownloadCompletedFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomsheetDownloadCompletedBinding
    private var videoName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBottomsheetDownloadCompletedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            videoName = it.getString("video_name")
        }
        binding.tvSuccessMessage.text = "$videoName is downloaded successfully"
        binding.btnGoToDownload.setOnClickListener {
            findNavController().navigate(R.id.DownloadFragment)
            dismiss()
        }
    }

}