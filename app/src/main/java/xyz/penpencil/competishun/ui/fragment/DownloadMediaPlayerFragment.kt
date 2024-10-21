package xyz.penpencil.competishun.ui.fragment

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
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.databinding.FragmentDownloadMediaPlayerBinding
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.main.HomeActivity
import java.io.File

@AndroidEntryPoint
class DownloadMediaPlayerFragment : DrawerVisibility() {

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

    @OptIn(UnstableApi::class)
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

        Log.e("url", "video_url:$videoUrl")
        Log.e("Title", "video_title:$title")

        if (title.isNotEmpty()) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
        }
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
//        binding.playerView.videoSurfaceView?.rotation = 90F;


        playVideo(videoUrl)

        try {
            val file = File(videoUrl)
            val uri = if (file.exists()) {
                Uri.fromFile(file)
            } else {
                Uri.parse(videoUrl)
            }

            Log.e("FilePath", "File exists: ${file.exists()}, Path: $file")
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e("PlayerSetup", "Failed to play video", e)
        }


        binding.qualityButton.setOnClickListener {
            showSpeedOrQualityDialog()
        }

    }

    private fun playVideo(videoUrl: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.playerView.visibility = View.GONE

        val uri = if (File(videoUrl).exists()) {
            Uri.fromFile(File(videoUrl))
        } else {
            Uri.parse(videoUrl)
        }

        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", "Playback Error: ${error.message}", error)
            }
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    binding.progressBar.visibility = View.GONE
                    binding.playerView.visibility = View.VISIBLE
                }
            }
        })
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
    override fun onResume() {
        super.onResume()
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun seekForward() {
        val position = player.currentPosition
        player.seekTo(minOf(position + SEEK_OFFSET_MS, player.duration))
    }

}