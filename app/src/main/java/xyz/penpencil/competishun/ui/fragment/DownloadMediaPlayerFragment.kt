package xyz.penpencil.competishun.ui.fragment

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.findNavController
import com.google.android.exoplayer2.ui.StyledPlayerView
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentDownloadMediaPlayerBinding
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import xyz.penpencil.competishun.utils.serializable
import xyz.penpencil.competishun.utils.toggleImmersiveMode
import java.io.File
import kotlin.random.Random


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

    private lateinit var mFullScreenDialog: Dialog
    private var mExoPlayerFullscreen: Boolean = false

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var flickeringText: TextView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }


    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        sharedViewModel = ViewModelProvider(requireActivity())[SharedVM::class.java]

        binding.backBtn.setOnClickListener {
            if (mExoPlayerFullscreen){
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                closeFullscreenDialog()
            }else {
                view?.findNavController()?.popBackStack()
            }
        }

        initFullscreenDialog()
        val videoData = arguments?.serializable<TopicContentModel>("VIDEO_DATA")
        val videoUrl = videoData?.url?:""
        val title = videoData?.topicName?:""
        val description = arguments?.getString("description") ?: ""
        binding.description.text = description


        if (title.isNotEmpty()) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
        }
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.useArtwork = true
        binding.playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        binding.playerView.player = player


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

        binding.fullScreen.setOnClickListener {
            toggleFullscreen()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mExoPlayerFullscreen){
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    closeFullscreenDialog()
                }else {
                    view?.findNavController()?.popBackStack()
                }
            }
        })

        sharedPreferencesManager.getString("ROLL_NUMBER", "")?.let {
            if (it.isNotEmpty()) {
                waterMark(it)
            }
        }
    }

    private val flickerRunnable = object : Runnable {
        override fun run() {
            flickeringText?.let { textView ->
                textView.visibility = if (Random.nextBoolean()) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

                val parent = textView.parent as? FrameLayout ?: return@let
                val parentWidth = parent.width
                val parentHeight = parent.height

                if (parentWidth == 0 || parentHeight == 0) {
                    handler.postDelayed(this, 300000)
                    return@let
                }

                val layoutParams = textView.layoutParams as FrameLayout.LayoutParams

                val maxLeft = (parentWidth - textView.width).coerceAtLeast(0)
                val maxTop = (parentHeight - textView.height).coerceAtLeast(0)

                layoutParams.leftMargin = Random.nextInt(0, maxLeft)
                layoutParams.topMargin = Random.nextInt(0, maxTop)
                textView.layoutParams = layoutParams
                val delay = Random.nextLong(300, 600)
                handler.postDelayed(this, delay)
            }
        }
    }

    private fun waterMark(s: String) {
        if (flickeringText == null) {
            flickeringText = TextView(requireContext()).apply {
                text = s
                textSize = 12f
                setPadding(20)
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(requireContext(), R.color.alfa))
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(30, 30, 30, 30)
                }
            }
            if (binding.playerView is FrameLayout) {
                binding.playerView.addView(flickeringText)
                handler.post(flickerRunnable)
            } else {
                Log.e("WaterMark", "playerView is not a FrameLayout")
            }
        }
    }



    private fun initFullscreenDialog() {
        mFullScreenDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        mFullScreenDialog.setOnDismissListener {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            closeFullscreenDialog()
        }
    }


    private fun playVideo(videoUrl: String) {
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
                    binding.playerView.visibility = View.VISIBLE
                }
            }
        })
    }
    private fun showSpeedOrQualityDialog() {
        val options = arrayOf("Speed")

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

    private fun openFullscreenDialog() {
        (binding.playerView.parent as? ViewGroup)?.removeView(binding.playerView)
        mFullScreenDialog.addContentView(
            binding.playerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mExoPlayerFullscreen = true
        mFullScreenDialog.show()
        binding.fullScreen.setImageResource(R.drawable.zoom_in_map_24)

        showNavigationBar()
    }

    private fun closeFullscreenDialog() {
        (binding.playerView.parent as? ViewGroup)?.removeView(binding.playerView)
        binding.playerRootApp.addView(binding.playerView)
        mExoPlayerFullscreen = false
        mFullScreenDialog.dismiss()
        binding.fullScreen.setImageResource(R.drawable.zoom_out_map_24)
        hideNavigationBar()
    }

    private fun toggleFullscreen() {
        if (!mExoPlayerFullscreen) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            openFullscreenDialog()
        } else {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            closeFullscreenDialog()
        }
    }

    private fun hideNavigationBar() {

        showSystemBars()
    }

    private fun showNavigationBar() {
        hideSystemBars()
    }

    fun hideSystemBars() {
        requireActivity().window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            val controller = WindowInsetsControllerCompat(it, it.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun showSystemBars() {
        requireActivity().window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            val controller = WindowInsetsControllerCompat(it, it.decorView)
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}