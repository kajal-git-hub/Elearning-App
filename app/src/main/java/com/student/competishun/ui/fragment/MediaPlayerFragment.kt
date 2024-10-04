package com.student.competishun.ui.fragment

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
    private lateinit var courseFolderContentId: String
    private lateinit var courseFolderContentIds: ArrayList<String>
    private lateinit var courseFolderContentNames: ArrayList<String>
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000
    private var urlVideo:String = ""
    private var videoFormat:String = "480p"
    private lateinit var sharedViewModel: SharedVM
    private val videourlViewModel: VideourlViewModel by viewModels()
    private var videoUrls: List<String> = listOf()
    private var videoTitles: ArrayList<String> = ArrayList()
    private var currentVideoIndex: Int = 0
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

    private val updateTask = object : Runnable {
        override fun run() {
            val watchedDuration = getWatchedDuration()
            val watchedDurationInSeconds = (player.currentPosition / 1000).toInt()
            sharedViewModel.setWatchedDuration(watchedDurationInSeconds)
            handler.postDelayed(this, updateInterval)
        }
    }
     fun getWatchedDuration(): String {
        val currentPosition = player.currentPosition

        val minutes = (currentPosition / 60000).toInt()
        val seconds = ((currentPosition % 60000) / 1000).toInt()
        Log.e("watchesdd",String.format("%02d:%02d", minutes, seconds))
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar: ProgressBar = binding.progressBar
        var qualityButton = binding.qualityButton

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val videoUrl = arguments?.getString("url") ?: return
        Log.e("howdfdf",videoUrl)
        val title = arguments?.getString("url_name") ?: return
        if (title != null) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
        }
        courseFolderContentId = arguments?.getString("ContentId")?: return
        courseFolderContentIds = arguments?.getStringArrayList("folderContentIds")?: return
        courseFolderContentNames = arguments?.getStringArrayList("folderContentNames")?: return
        Log.e("getfolderNamess",courseFolderContentNames.toString())
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


        try {
            changeQuality(videoFormat)
            val mediaItem = MediaItem.fromUri(videoUrl)
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
                            binding.playerView.visibility = View.VISIBLE
                            binding.upNextOverlay.visibility = View.GONE
                        }
                        Player.STATE_ENDED -> {
                            Log.e("videoEnded",player.toString())
                            binding.playerView.visibility = View.GONE
                            binding.upNextOverlay.visibility = View.VISIBLE
                            binding.startNextButton.setOnClickListener {
                                Log.e("nextButtonclick","nectButton")
                                playNextVideo()
                                binding.upNextOverlay.visibility = View.GONE
                            }
                            binding.cancelNextButton.setOnClickListener {
                                binding.upNextOverlay.visibility = View.GONE
                            }
                            // When the video ends, play the next one if available
                            Log.e("withoutclickclick","withoutclick")
                           // playNextVideo() //deffault calling on video end
                        }

                       Player.STATE_IDLE -> {
                            Log.e("videoEndedIdle",player.toString())
                            // Hide the progress bar in case of idle or ended states
                            binding.progressBar.visibility = View.GONE
                            binding.playerView.visibility = View.VISIBLE
                        }
                    }
                }
            })
           // playVideo(videoUrl)


        } catch (e: Exception) {
            Log.e("PlayerSetup", "Failed to initialize player", e)
        }
        Log.e("getcontentid",courseFolderContentId)

        binding.qualityButton.setOnClickListener {
            showSpeedOrQualityDialog()
        }

    }

    private fun initializePlaylist() {
        videoUrls = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        )

        videoTitles = courseFolderContentNames
    }

    fun playVideo(videoUrl: String,  startPosition: Long = 0L,videoTittle: String) {
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
                        // Hide the progress bar when video is ready to play
                        binding.progressBar.visibility = View.GONE
                        binding.playerView.visibility = View.VISIBLE
                        // Seek to the previous position if provided
                        if (startPosition > 0L) {
                            Log.e("PlayerErrors", startPosition.toString())
                            player.seekTo(startPosition)
                        }

                        player.play()
                        Log.e("dfafadf",player.toString())
                    }

                    Player.STATE_ENDED -> {
                        Log.e("videoEnded",player.toString())
                        binding.playerView.visibility = View.GONE
                        binding.upNextOverlay.visibility = View.VISIBLE
                        binding.nextVideoTitle.text = videoTittle
                        binding.nextVideoTime.text = "1 min"
                        binding.startNextButton.setOnClickListener {
                            playNextVideo()
                            binding.upNextOverlay.visibility = View.GONE
                            binding.upNextOverlay.visibility = View.GONE
                        }
                        binding.cancelNextButton.setOnClickListener {
                            binding.upNextOverlay.visibility = View.GONE
                        }
                        // When the video ends, play the next one if available
                        playNextVideo()
                    }

                    Player.STATE_ENDED, Player.STATE_IDLE -> {
                        Log.e("videoEndedIdle",player.toString())
                        // Hide the progress bar in case of idle or ended states
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

    private fun playNextVideo() {

       // initializePlaylist()
        videoUrls = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        )
        videourlViewModel.fetchVideoStreamUrl(courseFolderContentIds[currentVideoIndex], "480p")
        Log.e("foldfdfd",courseFolderContentId)
        videourlViewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                urlVideo = signedUrl

            } else {
                Log.e("url issues", signedUrl.toString())

            }
        }

        videoTitles = courseFolderContentNames
        if (currentVideoIndex < courseFolderContentIds.size - 1) {
            Log.e("currentVideoIndexSize",currentVideoIndex.toString())
            currentVideoIndex++
           // val nextVideoUrl = videoUrls[currentVideoIndex]
            val nextVideoUrl = urlVideo
            val nextVideoTittle = videoTitles[currentVideoIndex]
            binding.tittleBtn.text = nextVideoTittle
            playVideo(nextVideoUrl,0,nextVideoTittle)
            Log.e("currentVideoAfter",currentVideoIndex.toString())
        } else {
            // No more videos in the playlist
            Toast.makeText(requireContext(), "No more videos to play", Toast.LENGTH_SHORT).show()
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

    fun changeQuality(formate:String):String{
        videourlViewModel.fetchVideoStreamUrl(courseFolderContentId, formate)
        Log.e("foldfdfd",courseFolderContentId)
        videourlViewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                urlVideo = signedUrl

               // playVideo(signedUrl,currentPlaybackPosition)
            }else
            {
                Log.e("url issues", signedUrl.toString())

            }
        })
      return urlVideo
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
        val videoQualityOptions = arrayOf("480p", "720p", "1080p")
        val currentPlaybackPosition = player.currentPosition
        AlertDialog.Builder(requireContext())
            .setTitle("Select Video Quality")
            .setItems(videoQualityOptions) { dialog, which ->
                when (which) {
                    0 -> {
                        videoFormat = "480p"
                        changeQuality(videoFormat)
                        //playVideo(urlVideo,currentPlaybackPosition)
                        Toast.makeText(requireContext(), "Selected: 480p", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        videoFormat = "720p"
                        changeQuality(videoFormat)
                      //  playVideo(urlVideo,currentPlaybackPosition)
                        Toast.makeText(requireContext(), "Selected: 720p", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        videoFormat = "1080p"
                        changeQuality(videoFormat)
                      //  playVideo(urlVideo,currentPlaybackPosition)
                        Toast.makeText(requireContext(), "Selected: 1080p", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
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
            Log.e("destroyy",player.toString())
            player.release()
        }

        handler.removeCallbacks(updateTask)
        handler.removeCallbacksAndMessages(null)
        // updateSeekBarHandler.removeCallbacks(updateSeekBarRunnable)
        // Reset the screen orientation to the user's preference
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        //Log.e("kjlajfl",player.toString())
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


    private fun seekForward() {
        val position = player.currentPosition
        player.seekTo(minOf(position + SEEK_OFFSET_MS, player.duration))
    }
}
