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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
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
        val url = arguments?.getString("url")
        Log.e("VideoUrlExplore", url ?: "URL is null")
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }


    private val updateTask = object : Runnable {
        override fun run() {
            val watchedDuration = getWatchedDuration()
                val watchedDurationInSeconds = (player.currentPosition / 1000).toInt()
                sharedViewModel.setWatchedDuration(watchedDurationInSeconds)
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("MediaPlayerFragment", "onViewCreated called") // Add this line

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val videoUrl = arguments?.getString("url") ?: return
        val tittle = arguments?.getString("url_name") ?: return
        if (tittle!=null){
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = tittle
        }
        courseFolderContentId = arguments?.getString("ContentId") ?: return
        Log.e("VideoUrlExplore", videoUrl)

        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
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
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            Log.e("MediaPlayerFragment", "Player prepared, calling play()")
            player.play()
        } catch (e: Exception) {
            Log.e("PlayerSetup", "Failed to initialize player", e)
        }

        binding.qualityButton.setOnClickListener {
            showQualityDialog(player)
        }
        gestureDetector = GestureDetector(requireContext(), DoubleTapGestureListener())
        binding.playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.playerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @OptIn(UnstableApi::class)
    private fun showQualityDialog(  player: ExoPlayer) {
        val qualities = getAvailableQualities(player)
        val speeds = listOf("0.5x", "1.0x", "1.5x", "1.75x", "2.0x")


        if (qualities.isEmpty() && speeds.isEmpty()) {
            Toast.makeText(requireContext(), "No video qualities available", Toast.LENGTH_SHORT).show()
            return
        }

        val qualityLabels = qualities.map { it.first }.toTypedArray()
        val speedLabels = speeds.toTypedArray()

        val combinedLabels = qualityLabels + speedLabels


        AlertDialog.Builder(requireContext())
            .setTitle("Select Video Quality or Speed")
            .setItems(combinedLabels) { _, which ->
                if (which < qualities.size) {
                    // User selected a quality
                    val selectedQuality = qualities[which].second
                    switchToQuality(selectedQuality)
                } else {
                    val selectedSpeed = speeds[which - qualities.size]
                    setPlaybackSpeed(selectedSpeed)
                }
            }
            .show()
    }
    private fun setPlaybackSpeed(speed: String) {
        try {
            val speedValue = speed.replace("x", "").toFloat()
            player.setPlaybackSpeed(speedValue)
        } catch (e: NumberFormatException) {
            Log.e("MediaPlayerFragment", "Invalid speed format: $speed", e)
        }
    }


    @OptIn(UnstableApi::class)
    fun getAvailableQualities(player: ExoPlayer): List<Pair<String, DefaultTrackSelector.SelectionOverride>> {
        val qualities = mutableListOf<Pair<String, DefaultTrackSelector.SelectionOverride>>()

        val trackSelector = player.trackSelector as DefaultTrackSelector
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return qualities

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            if (player.getRendererType(rendererIndex) == C.TRACK_TYPE_VIDEO) {
                val trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)

                for (groupIndex in 0 until trackGroups.length) {
                    val trackGroup = trackGroups[groupIndex]

                    for (trackIndex in 0 until trackGroup.length) {
                        val format = trackGroup.getFormat(trackIndex)
                        if (format.height != Format.NO_VALUE) {
                            val resolutionLabel = "${format.height}p"
                            Log.d("AvailableQualities", "TrackIndex: $trackIndex, Resolution: ${format.height}p, Bitrate: ${format.bitrate}, MimeType: ${format.sampleMimeType}")
                            val selectionOverride = DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex)
                            qualities.add(Pair(resolutionLabel, selectionOverride))
                        }
                    }
                }
            }
        }
        return qualities
    }

    @OptIn(UnstableApi::class)
    private fun switchToQuality(selectionOverride: DefaultTrackSelector.SelectionOverride) {
        val trackSelector = player.trackSelector as DefaultTrackSelector
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return

        for (i in 0 until mappedTrackInfo.rendererCount) {
            if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                val parametersBuilder = trackSelector.parameters.buildUpon()
                parametersBuilder.setSelectionOverride(i, mappedTrackInfo.getTrackGroups(i), selectionOverride)
                trackSelector.setParameters(parametersBuilder)
                break
            }
        }
    }

    private inner class DoubleTapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val x = e.x
            val width = binding.playerView.width
            val centerThirdStart = width / 3
            val centerThirdEnd = 2 * width / 3

            when {
                x.toInt() in centerThirdStart..centerThirdEnd -> {
                    if (player.isPlaying) {
                        player.pause()
                        Log.d("GestureDetection", "Player paused")
                    } else {
                        player.play()
                        Log.d("GestureDetection", "Player resumed")
                    }
                }
                x < centerThirdStart -> {
                    seekBack()
                    Log.d("GestureDetection", "Rewind 10 seconds")
                }
                else -> {
                    seekForward()
                    Log.d("GestureDetection", "Fast forward 10 seconds")
                }
            }
            val watchedDuration = getWatchedDuration()
            return true
        }
    }

    private fun seekBack() {
        val position = player.currentPosition
        val newPosition = maxOf(position - 10000, 0)
        player.seekTo(newPosition)
    }

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
            videourlViewModel.updateVideoProgressResult.observe(viewLifecycleOwner) { success ->
                if (success) {

                    Log.e("Video updated ",success.toString())
                } else {
                    Log.e("failed Video updated ","video not updated")
                }
            }
            videourlViewModel.updateVideoProgress(courseFolderContentId, currentDuration)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::player.isInitialized) {
            player.release()
        }
        handler.removeCallbacks(updateTask)
        handler.removeCallbacksAndMessages(null)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}
