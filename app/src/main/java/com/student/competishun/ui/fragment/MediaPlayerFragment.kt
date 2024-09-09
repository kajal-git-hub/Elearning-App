package com.student.competishun.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import androidx.navigation.fragment.findNavController
import com.otaliastudios.zoom.ZoomApi
import com.otaliastudios.zoom.ZoomLayout
import com.student.competishun.R
import com.student.competishun.databinding.FragmentMediaPlayerBinding
import com.student.competishun.di.SharedVM
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.VideourlViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlayerFragment : Fragment() {

    private lateinit var binding: FragmentMediaPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var gestureDetector: GestureDetector
    private lateinit var zoomLayout: ZoomLayout
    private var courseFolderContentId:String = ""
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedViewModel: SharedVM
    private val updateInterval: Long = 5000 // 5 seconds
    private val videourlViewModel: VideourlViewModel by viewModels()
    companion object {
        private const val SEEK_OFFSET_MS = 10000L // 10 seconds in milliseconds
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }


    private val updateTask = object : Runnable {
        override fun run() {
            // Get the watched duration
            val watchedDuration = getWatchedDuration()
           // val watchedDurationInSeconds = convertTimeToSeconds(watchedDuration)
             // Check if initialized
                val watchedDurationInSeconds = (player.currentPosition / 1000).toInt()

                sharedViewModel.setWatchedDuration(watchedDurationInSeconds)

            // Send the watched duration to the other fragment
           // videoProgress(courseFolderContentId, watchedDuration.toInt())

            // Schedule the next update
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide navigation and floating button
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        // Initialize SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)

        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                player.release() // Release player
                findNavController().navigateUp()
            }
        })

        val url = arguments?.getString("url") ?: return
        courseFolderContentId = arguments?.getString("ContentId") ?: return
        Log.e("VideoUrl", url)

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        // Setup video progress update task
        handler.post(object : Runnable {
            override fun run() {
                if (isAdded && ::player.isInitialized) {
                    val watchedDurationInSeconds = (player.currentPosition / 1000).toInt()
                    videoProgress(courseFolderContentId, watchedDurationInSeconds)
                    handler.postDelayed(this, updateInterval)
                }
            }
        })

        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", "Playback Error: ${error.message}", error)
            }
        })

        try {
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e("PlayerSetup", "Failed to initialize player", e)
        }

        // Set up double-tap gesture for playback control
        gestureDetector = GestureDetector(requireContext(), DoubleTapGestureListener())
        binding.playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        // Handle system window insets for proper layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.playerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



    // Double-tap gesture listener for playback control
    private inner class DoubleTapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val x = e.x
            val width = binding.playerView.width
            val centerThirdStart = width / 3
            val centerThirdEnd = 2 * width / 3

            when {
                x.toInt() in centerThirdStart..centerThirdEnd -> {
                    // Center third: Toggle play/pause
                    if (player.isPlaying) {
                        player.pause()
                        Log.d("GestureDetection", "Player paused")
                    } else {
                        player.play()
                        Log.d("GestureDetection", "Player resumed")
                    }
                }
                x < centerThirdStart -> {
                    // Left third: Rewind by 10 seconds
                    seekBack()
                    Log.d("GestureDetection", "Rewind 10 seconds")
                }
                else -> {
                    // Right third: Fast forward by 10 seconds
                    seekForward()
                    Log.d("GestureDetection", "Fast forward 10 seconds")
                }
            }
            val watchedDuration = getWatchedDuration()

            return true
        }
    }



    // Seek back by 10 seconds
    private fun seekBack() {
        val position = player.currentPosition
        val newPosition = maxOf(position - 10000, 0)
        player.seekTo(newPosition)
    }

    // Seek forward by 10 seconds
    private fun seekForward() {
        val position = player.currentPosition
        val duration = player.duration
        val newPosition = minOf(position + 10000, duration)
        player.seekTo(newPosition)
    }

    private fun sendWatchedDurationToOtherFragment(watchedDuration: Int) {
        val bundle = Bundle().apply {
            putInt("watchedDuration", watchedDuration)
        }
    findNavController().navigate(R.id.SubjectContentFragment,bundle)
}



    private fun getWatchedDuration(): String {
        val currentPosition = player.currentPosition

        val minutes = (currentPosition / 60000).toInt()
        val seconds = ((currentPosition % 60000) / 1000).toInt()
        Log.e("watchesdd",String.format("%02d:%02d", minutes, seconds))
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun videoProgress(courseFolderContentId:String,currentDuration:Int) {

        if (isAdded) {
            // Observe the result of the updateVideoProgress mutation
            videourlViewModel.updateVideoProgressResult.observe(viewLifecycleOwner) { success ->
                if (success) {

                    Log.e("Video updated ",success.toString())
                } else {
                    Log.e("failed Video updated ","video not updated")
                }
            }

            // Call the mutation
            videourlViewModel.updateVideoProgress(courseFolderContentId, currentDuration)
        }
    }

    private fun convertTimeToSeconds(timeString: String): Int {
        val timeParts = timeString.split(":")
        if (timeParts.size != 2) return 0 // Invalid format

        val minutes = timeParts[0].toIntOrNull() ?: return 0
        val seconds = timeParts[1].toIntOrNull() ?: return 0
        return minutes * 60 + seconds
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::player.isInitialized) {
            player.release()
        }
        handler.removeCallbacks(updateTask)
        handler.removeCallbacksAndMessages(null)
        // updateSeekBarHandler.removeCallbacks(updateSeekBarRunnable)
        // Reset the screen orientation to the user's preference
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}
