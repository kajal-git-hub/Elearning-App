package com.student.competishun.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
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
import androidx.navigation.fragment.findNavController
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
    private var courseFolderContentId: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedViewModel: SharedVM
    private val videourlViewModel: VideourlViewModel by viewModels()

    companion object {
        private const val SEEK_OFFSET_MS = 10000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var qualityButton = binding.qualityButton

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val videoUrl = arguments?.getString("url") ?: return
        val title = arguments?.getString("url_name") ?: return
        if (title != null) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
        }
        courseFolderContentId = arguments?.getString("ContentId") ?: return

        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        binding.qualityButton.setOnClickListener {
            showSpeedOrQualityDialog()
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

    private fun showSpeedOrQualityDialog() {
        val options = arrayOf("Speed", "Quality")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showSpeedSelectionDialog()
                    1 -> showQualityDialog()
                }
            }
            .show()
    }

    private fun showSpeedSelectionDialog() {
        val speeds = arrayOf("0.5x", "1.0x", "1.5x", "1.75x", "2.0x")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Playback Speed")
            .setItems(speeds) { _, which ->
                val speed = speeds[which].replace("x", "").toFloat()
                player.setPlaybackSpeed(speed)
            }
            .show()
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun showQualityDialog() {
        val qualities = getAvailableQualities()

        if (qualities.isEmpty()) {
            Toast.makeText(requireContext(), "No video qualities available", Toast.LENGTH_SHORT).show()
            return
        }

        val qualityLabels = qualities.map { it.first }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select Video Quality")
            .setItems(qualityLabels) { _, which ->
                val selectedQuality = qualities[which].second
                switchToQuality(selectedQuality)
            }
            .show()
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun getAvailableQualities(): List<Pair<String, DefaultTrackSelector.SelectionOverride>> {
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
                            val selectionOverride = DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex)
                            qualities.add(Pair(resolutionLabel, selectionOverride))
                        }
                    }
                }
            }
        }
        return qualities
    }

    @androidx.annotation.OptIn(UnstableApi::class)
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (::player.isInitialized) {
            player.release()
        }
        handler.removeCallbacksAndMessages(null)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
                    } else {
                        player.play()
                    }
                }
                x < centerThirdStart -> seekBack()
                else -> seekForward()
            }
            return true
        }
    }

    private fun seekBack() {
        val position = player.currentPosition
        player.seekTo(maxOf(position - SEEK_OFFSET_MS, 0))
    }

    private fun seekForward() {
        val position = player.currentPosition
        player.seekTo(minOf(position + SEEK_OFFSET_MS, player.duration))
    }
}
