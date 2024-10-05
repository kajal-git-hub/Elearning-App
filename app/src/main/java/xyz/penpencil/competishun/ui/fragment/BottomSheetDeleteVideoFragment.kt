package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentBottomSheetDeleteVideoBinding
import xyz.penpencil.competishun.utils.OnDeleteClickListener

class BottomSheetDeleteVideoFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetDeleteVideoBinding
    private var listener: OnDeleteClickListener? = null
    private var itemPosition: Int = -1
    private var itemDetails: TopicContentModel? = null

    fun setListener(listener: OnDeleteClickListener, position: Int, item: TopicContentModel) {
        this.listener = listener
        this.itemPosition = position
        this.itemDetails = item
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
        binding = FragmentBottomSheetDeleteVideoBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvATDTopicName.text = itemDetails?.topicName

        binding.clDeleteButton.setOnClickListener {
            if (itemPosition >= 0) {
                listener?.onDeleteClick(itemPosition,itemDetails!!)
            }
            dismiss()
        }
        binding.clCancelButton.setOnClickListener {
            dismiss()
        }
    }


}