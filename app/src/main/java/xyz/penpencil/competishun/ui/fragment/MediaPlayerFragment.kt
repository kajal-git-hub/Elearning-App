package xyz.penpencil.competishun.ui.fragment

import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.otaliastudios.zoom.ZoomLayout
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentMediaPlayerBinding
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File

@AndroidEntryPoint
class MediaPlayerFragment : DrawerVisibility() {

    private lateinit var binding: FragmentMediaPlayerBinding
    private lateinit var player: ExoPlayer
    private var itemDetails: TopicContentModel? = null
    private lateinit var gestureDetector: GestureDetector
    private lateinit var zoomLayout: ZoomLayout
    private lateinit var courseFolderContentId: String
    private lateinit var courseFolderContentIds: ArrayList<String>
    private lateinit var courseFolderContentNames: ArrayList<String>
    private lateinit var courseFolderContentDescs: ArrayList<String>
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000
    private var urlVideo:String = ""
    private var isZoomed = false
    private var videoFormat:String = "480p"
    private lateinit var sharedViewModel: SharedVM
    private val videourlViewModel: VideourlViewModel by viewModels()
    private var videoUrls: List<String> = listOf()
    private var videoTitles: ArrayList<String> = ArrayList()
    private var videoDescs: ArrayList<String> = ArrayList()
    private var currentVideoIndex: Int = 0
    var fileName: String = ""
    var videoUrl: String = ""
    var videoFile : File?=null
    companion object {
        private const val SEEK_OFFSET_MS = 10000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar: ProgressBar = binding.progressBar
        var qualityButton = binding.qualityButton

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        binding.backBtn.setOnClickListener {
           findNavController().popBackStack()
        }

        val videoUrl = arguments?.getString("url") ?: return
        Log.e("howdfdf",videoUrl)
        val title = arguments?.getString("url_name") ?: return
        courseFolderContentDescs = arguments?.getStringArrayList("folderContentDescs")?: arrayListOf()
        if (title != null) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
            binding.tittleTv.text = title
            if (courseFolderContentDescs.isNotEmpty()){
                binding.descTv.text = courseFolderContentDescs[0]
            }
        }

        courseFolderContentId = arguments?.getString("ContentId")?: return
        courseFolderContentIds = arguments?.getStringArrayList("folderContentIds")?: return
        courseFolderContentNames = arguments?.getStringArrayList("folderContentNames")?: return

        Log.e("getfolderNamess",courseFolderContentNames.toString())
        Log.e("getfolderDess",courseFolderContentDescs.toString())
        player = ExoPlayer.Builder(requireContext()).build()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
        binding.playerView.player = player
        binding.fullscreenButton.setOnClickListener {
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            Log.e("landscapemode",isLandscape.toString())
            if (isLandscape) {
               Log.e("landscape mode",isLandscape.toString())
                binding.fullscreenButton.visibility = View.VISIBLE
                binding.playerView.layoutParams = binding.playerView.layoutParams.apply {
                    height = (300 * resources.displayMetrics.density).toInt()
                }// Convert 300dp to pixels
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT // Reset or use any mode you want in portrait
            } else {
                binding.fullscreenButton.visibility = View.VISIBLE
                Log.e("landscapeport",isLandscape.toString())
                // If in portrait, switch to landscape
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM // Zoom for full-screen landscape
            }
        }
        // Setup video progress update task
/*        handler.post(object : Runnable {
            override fun run() {
                if (isAdded && ::player.isInitialized) {
                    val watchedDurationInSeconds = (player.currentPosition / 1000).toInt()
                    videoProgress(courseFolderContentId, watchedDurationInSeconds)
                    handler.postDelayed(this, updateInterval)
                }
            }
        })*/
        try {
            changeQuality(videoFormat)
            val mediaItem = MediaItem.fromUri(videoUrl)
            Log.e("vidoeurl",videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            setupZoomFeature()
            player.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("PlayerError", "Playback Error: ${error.message}", error)
                }
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> {
                            binding.progressBar.visibility = View.GONE
                            binding.playerView.visibility = View.VISIBLE
                            binding.upNextOverlay.visibility = View.GONE
                            player.play()
                        }
                        Player.STATE_ENDED -> {
                            Log.e("videoEnded",player.toString())
                            binding.playerView.visibility = View.GONE
                            binding.upNextOverlay.visibility = View.VISIBLE
                            videoTitles = courseFolderContentNames
                            videoDescs = courseFolderContentDescs
                            if (currentVideoIndex < courseFolderContentIds.size - 1) {
                                Log.e("currentVideoIndexSize", currentVideoIndex.toString())
                                currentVideoIndex++
                                val nextVideoTittle = videoTitles[currentVideoIndex]
                                val nextVideoDesc = videoDescs[currentVideoIndex]
                                binding.nextVideoTitle.text = nextVideoTittle
                                binding.descTv.text = nextVideoDesc
                                binding.tittleTv.text = nextVideoTittle
                             } else {
                        // No more videos in the playlist
                        Toast.makeText(requireContext(), "No more videos to play", Toast.LENGTH_SHORT).show()
                             }
                            binding.startNextButton.setOnClickListener {
                                Log.e("nextButtonclick",urlVideo)
                                playNextVideo()
                                binding.upNextOverlay.visibility = View.GONE
                            }
                            binding.cancelNextButton.setOnClickListener {
                                binding.upNextOverlay.visibility = View.GONE
                                findNavController().popBackStack()
                            }
                            // When the video ends, play the next one if available
                            Log.e("withoutclickclick","withoutclick")
                           // playNextVideo() //deffault calling on video end
                        }

//                       Player.STATE_IDLE -> {
//                            Log.e("videoEndedIdle",player.toString())
//                            // Hide the progress bar in case of idle or ended states
////                            binding.progressBar.visibility = View.VISIBLE
////                            binding.playerView.visibility = View.VISIBLE
//                        }
                        Player.STATE_BUFFERING->{
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            })


        } catch (e: Exception) {
            Log.e("PlayerSetup", "Failed to initialize player", e)
        }
        Log.e("getcontentid",courseFolderContentId)

        binding.qualityButton.setOnClickListener {
            showSpeedOrQualityDialog()
        }

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view.findNavController().popBackStack()
                }
            })

    }

    private fun storeItemInPreferences(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItem(item)
    }

    private fun storeItemInPreferencesBm(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItemBm(item)
    }

    private fun setupZoomFeature() {
        binding.fullscreenButton.setOnClickListener {
            toggleZoom()
        }
    }


    @androidx.annotation.OptIn(UnstableApi::class)
    private fun toggleZoom() {
        isZoomed = !isZoomed
        val layoutParams =  binding.playerView.layoutParams

        // Toggle between original size and full-screen
        if (isZoomed) {
            Log.e("uszibivd",isZoomed.toString())
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
          //  isZoomed = false
        } else {
            Log.e("uszibelse",isZoomed.toString())
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
            layoutParams.height = resources.getDimensionPixelSize(R.dimen.original_height) // original height
        }

        binding.playerView.layoutParams = layoutParams
    }

    fun playVideo(videoUrl: String,  startPosition: Long = 0L,videoTittle: String,videoDesc: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.playerView.visibility = View.GONE
        val mediaItem = MediaItem.fromUri(videoUrl)
        Log.e("getting $startPosition $videoTittle",videoUrl)
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
//v1 - 45s v2 - 5min, v3 - 19s
                        player.play()
                        Log.e("dfafadf",player.toString())
                    }

                    Player.STATE_ENDED -> {
                        Log.e("videoEnded",player.toString())
                        binding.playerView.visibility = View.GONE
                        binding.upNextOverlay.visibility = View.VISIBLE
                        binding.nextVideoTitle.text = videoTittle
                        binding.descTv.text = videoDesc
                        binding.nextVideoTime.text = "1 min"
                        binding.startNextButton.setOnClickListener {
                            playNextVideo()
                            binding.upNextOverlay.visibility = View.GONE
                        }
                        binding.cancelNextButton.setOnClickListener {
                            binding.upNextOverlay.visibility = View.GONE
                        }
                    }

                }
            }
        })
//        gestureDetector = GestureDetector(requireContext(), DoubleTapGestureListener())
//        binding.playerView.setOnTouchListener { _, event ->
//            gestureDetector.onTouchEvent(event)
//            true
//        }
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.playerView) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    private fun playNextVideo() {

       // initializePlaylist()
        videourlViewModel.fetchVideoStreamUrl(courseFolderContentIds[currentVideoIndex], "480p")
        Log.e("foldfdfd",courseFolderContentId)
        videourlViewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            urlVideo = ""
            Log.d("NextURL", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                videoTitles = courseFolderContentNames
                videoDescs = courseFolderContentDescs
                // val nextVideoUrl = videoUrls[currentVideoIndex]
                val nextVideoUrl = urlVideo
                val nextVideoTittle = videoTitles[currentVideoIndex]
                val nextVideoDesc = videoDescs[currentVideoIndex]
                binding.tittleBtn.text = nextVideoTittle
                binding.tittleTv.text = nextVideoTittle
                binding.descTv.text = nextVideoDesc
                playVideo(signedUrl,0,nextVideoTittle,nextVideoDesc)
                urlVideo = signedUrl

            } else {
                Log.e("url issues", urlVideo.toString())

            }
        }
            Log.e("currentVideoAfter",currentVideoIndex.toString())
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
        Log.e("APIcontentId",courseFolderContentId)
        videourlViewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                urlVideo = signedUrl
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
                        Toast.makeText(requireContext(), "Selected: 480p", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        videoFormat = "720p"
                        changeQuality(videoFormat)
                        Toast.makeText(requireContext(), "Selected: 720p", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        videoFormat = "1080p"
                        changeQuality(videoFormat)
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
        urlVideo = ""
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
