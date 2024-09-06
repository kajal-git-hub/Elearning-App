package com.student.competishun.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.otaliastudios.zoom.ZoomApi
import com.otaliastudios.zoom.ZoomLayout
import com.student.competishun.databinding.FragmentMediaPlayerBinding
import com.student.competishun.ui.main.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlayerFragment : Fragment() {

    private lateinit var binding: FragmentMediaPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var gestureDetector: GestureDetector
    private lateinit var zoomLayout: ZoomLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        Log.d("reachedd","reacheddddd")

        val url = arguments?.getString("url")
        Log.e("viddeourl",url.toString())
        //initialize exoplayer
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
        binding.igToolbarBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val mediaItem = MediaItem.fromUri(url!!)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()


        gestureDetector = GestureDetector(requireContext(), DoubleTapGestureListener())

        // Apply gesture detector to player view
        binding.playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                player.release()
                findNavController().navigateUp()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(binding.playerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private inner class DoubleTapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val x = e.x
            val width = binding.playerView.width

            if (x < width / 2) {
                // Rewind
                player.seekBack()
            } else {
                // Fast forward
                player.seekForward()
            }
            return true

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
        // Reset the screen orientation to the user's preference
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
