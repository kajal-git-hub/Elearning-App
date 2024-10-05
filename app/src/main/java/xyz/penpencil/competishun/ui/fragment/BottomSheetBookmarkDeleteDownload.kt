package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentBottomSheetBookmarkDeleteDownloadBinding

class BottomSheetBookmarkDeleteDownload(
    private val listener: OnDeleteItemListener,
) : BottomSheetDialogFragment() {
    private var position: Int = -1
    private lateinit var item: TopicContentModel
    private lateinit var binding : FragmentBottomSheetBookmarkDeleteDownloadBinding


    fun setItem(position: Int, item: TopicContentModel) {
        this.position = position
        this.item = item
    }

    interface OnDeleteItemListener {
        fun onDeleteItem(position: Int,item: TopicContentModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBookmarkDeleteDownloadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBmDownloadThing.setOnClickListener {
            val bottomSheetDownloadOptions = BottomSheetVideoQualityFragment()
            bottomSheetDownloadOptions.show(childFragmentManager, bottomSheetDownloadOptions.tag)
        }
        binding.tvBmDeleteThing.setOnClickListener {
            listener.onDeleteItem(position,item)
            dismiss()
        }

    }

}