package com.student.competishun.ui.fragment

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.otaliastudios.zoom.ZoomLayout
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.databinding.FragmentDownloadMediaPlayerBinding
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import java.io.File

@AndroidEntryPoint
class DownloadMediaPlayerFragment : Fragment() {

    private lateinit var binding: FragmentDownloadMediaPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var gestureDetector: GestureDetector
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedViewModel: SharedVM
    companion object {
        private const val SEEK_OFFSET_MS = 10000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentDownloadMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val videoUrl = arguments?.getString("url") ?: return
        val title = arguments?.getString("url_name") ?: return
        Log.e("url","video_url:"+videoUrl)
        Log.e("url","video_title:"+title)

        if (title != null) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
        }
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        try {
            val file = File(context?.filesDir, videoUrl)
            Log.e("FilePath", "File exists: ${file.exists()}, Path: ${file.absolutePath}")
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e("PlayerSetup", "Failed to play video from local storage", e)
        }


        binding.qualityButton.setOnClickListener {
            showSpeedOrQualityDialog()
        }

    }

    fun playVideo(videoUrl: String,  startPosition: Long = 0L) {
        binding.progressBar.visibility = View.VISIBLE
        binding.playerView.visibility = View.GONE
        val mediaItem = MediaItem.fromUri(videoUrl)
        Log.e("getting $startPosition",videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", "Playback Error: ${error.message}", error)
            }
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        binding.progressBar.visibility = View.GONE
                        binding.playerView.visibility = View.VISIBLE
                        if (startPosition > 0L) {
                            Log.e("PlayerErrors", startPosition.toString())
                            player.seekTo(startPosition)
                        }

                        player.play()
                        Log.e("dfafadf",player.toString())
                    }

                    Player.STATE_ENDED, Player.STATE_IDLE -> {
                        binding.progressBar.visibility = View.GONE
                        binding.playerView.visibility = View.VISIBLE
                    }
                }
            }
        })
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