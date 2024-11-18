package xyz.penpencil.competishun.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
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
import com.otaliastudios.zoom.ZoomLayout
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentMediaPlayerBinding
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.util.Locale
import kotlin.random.Random

@AndroidEntryPoint
class MediaPlayerFragment : DrawerVisibility() {

    private lateinit var binding: FragmentMediaPlayerBinding
    private lateinit var player: ExoPlayer
    private var itemDetails: TopicContentModel? = null
    private lateinit var gestureDetector: GestureDetector
    private lateinit var zoomLayout: ZoomLayout
    private lateinit var courseFolderContentId: String
    private lateinit var homeworkNames: ArrayList<String>
    private lateinit var homeworkDescs: ArrayList<String>
    private lateinit var courseFolderContentIds: ArrayList<String>
    private lateinit var homeworkLinks: ArrayList<String>
    private lateinit var courseFolderContentNames: ArrayList<String>
    private lateinit var courseFolderContentDescs: ArrayList<String>
    private val handler = Handler(Looper.getMainLooper())
    private val mHandler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000
    private var urlVideo:String = ""
    private lateinit var helperFunctions: HelperFunctions
    private var isZoomed = false
    private var videoFormat:String = "480p"
    private lateinit var sharedViewModel: SharedVM
    private val videourlViewModel: VideourlViewModel by viewModels()

    private var videoTitles: ArrayList<String> = ArrayList()
    private var videoDescs: ArrayList<String> = ArrayList()
    private var currentVideoIndex: Int = 0
    var fileName: String = ""

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var flickeringText: TextView ?=null

    private lateinit var mFullScreenDialog: Dialog
    private var mExoPlayerFullscreen: Boolean = false

    private var fullScreenButton: ImageView?=null
    private var backBtn: ImageView?=null
    private var qualityButton: ImageView?=null

    private var folderName = ""

    companion object {
        private const val SEEK_OFFSET_MS = 10000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        return String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helperFunctions = HelperFunctions()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedVM::class.java]

        val videoUrl = arguments?.getString("url") ?: ""
        Log.e("howdfdf",videoUrl)
        val title = arguments?.getString("url_name") ?: ""
        folderName = arguments?.getString("folderName") ?: ""

        courseFolderContentDescs = arguments?.getStringArrayList("folderContentDescs")?: arrayListOf()
        homeworkLinks = arguments?.getStringArrayList("homeworkLinks")?: arrayListOf()
        homeworkNames = arguments?.getStringArrayList("homeworkNames")?: arrayListOf()
        homeworkDescs = arguments?.getStringArrayList("homeworkDescs")?: arrayListOf()
        if (title != null) {
            binding.tittleBtn.visibility = View.VISIBLE
            binding.tittleBtn.text = title
            binding.tittleTv.text = title
            if (courseFolderContentDescs.isNotEmpty()){
                binding.descTv.text = courseFolderContentDescs[0]
                binding.homeworkDescTv.text = if (homeworkNames[0].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkNames[0]) else "NA"
                binding.homeworkDescTv.setOnClickListener {
                    val intent = Intent(context, PdfViewActivity::class.java).apply {
                        putExtra("PDF_URL", removeBrackets(homeworkLinks[0]))
                        putExtra("PDF_TITLE",homeworkNames[0])
                        putExtra("FOLDER_NAME",folderName)
                    }
                    context?.startActivity(intent)
//                    helperFunctions.downloadPdfOld(requireContext(),homeworkLinks[0],homeworkNames[0])
                }
                binding.homeworkDescTv.text = if (homeworkNames[0].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkNames[0]) else "NA"
                binding.homeworktittleTv.text = if (homeworkDescs[0].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkDescs[0]) else "NA"
            }else {
                binding.homeworkDescTv.visibility = View.GONE
                binding.homeworktittleTv.visibility = View.GONE
            }
        }
        courseFolderContentId = arguments?.getString("ContentId")?: ""
        courseFolderContentIds = arguments?.getStringArrayList("folderContentIds")?: arrayListOf()
        courseFolderContentNames = arguments?.getStringArrayList("folderContentNames")?: arrayListOf()
        homeworkLinks = arguments?.getStringArrayList("homeworkLinks")?: arrayListOf()
        homeworkNames = arguments?.getStringArrayList("homeworkNames")?: arrayListOf()

        Log.e("getfolderNamess",courseFolderContentNames.toString())
        Log.e("getfolderDess",courseFolderContentDescs.toString())
        player = ExoPlayer.Builder(requireContext()).build()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
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
            Log.e("vidoeurl",videoUrl)
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
                            binding.upNextOverlay.visibility = View.GONE
                            player.play()
                        }
                        Player.STATE_ENDED -> {
                            if (mExoPlayerFullscreen)
                                closeFullscreenDialog()
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
                                val nextVideohomework = homeworkNames[currentVideoIndex]
                                binding.nextVideoTitle.text = nextVideoTittle
                                binding.descTv.text = nextVideoDesc?:""
                                binding.tittleTv.text = nextVideoTittle
                                binding.homeworktittleTv.text = if (homeworkDescs[currentVideoIndex].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkDescs[currentVideoIndex]) else "NA"
                                binding.homeworkDescTv.text = if (homeworkNames[currentVideoIndex].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkNames[currentVideoIndex]) else "NA"
                                binding.homeworkDescTv.setOnClickListener {
                                    val intent = Intent(context, PdfViewActivity::class.java).apply {
                                        putExtra("PDF_URL", removeBrackets(homeworkLinks[currentVideoIndex]))
                                        putExtra("PDF_TITLE",homeworkNames[currentVideoIndex])
                                        putExtra("FOLDER_NAME",folderName)
                                    }
                                    context?.startActivity(intent)
//                                    helperFunctions.downloadPdfOld(requireContext(),homeworkLinks[currentVideoIndex],homeworkNames[currentVideoIndex])
                                }
                             } else {
                        // No more videos in the playlist
                        Toast.makeText(requireContext(), "No more videos to play", Toast.LENGTH_SHORT).show()
                             }
                            binding.nextProgress.setOnClickListener {
                                Log.e("nextButtonclick",urlVideo)
                                playNextVideo()
                                binding.upNextOverlay.visibility = View.GONE
                            }
                            binding.cancelNextButton.setOnClickListener {
                                binding.upNextOverlay.visibility = View.GONE
                                if (mExoPlayerFullscreen){
                                    closeFullscreenDialog()
                                }
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

        fullScreenButton = binding.playerView.findViewById<ImageButton>(R.id.fullScreen)
        backBtn = binding.playerView.findViewById<ImageButton>(R.id.back_btn)
        qualityButton = binding.playerView.findViewById<ImageButton>(R.id.qualityButton)

        qualityButton?.setOnClickListener {
            showSpeedOrQualityDialog()
        }

        binding.download.setOnClickListener {  }
        binding.bookmark.setOnClickListener {  }

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
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

        initFullscreenDialog()

        fullScreenButton?.setOnClickListener {
            toggleFullscreen()
        }
        backBtn?.setOnClickListener {
           if (mExoPlayerFullscreen){
               requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
               closeFullscreenDialog()
           }else {
               view?.findNavController()?.popBackStack()
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
                    mHandler.postDelayed(this, 300000)
                    return@let
                }

                val layoutParams = textView.layoutParams as FrameLayout.LayoutParams

                val maxLeft = (parentWidth - textView.width).coerceAtLeast(0)
                val maxTop = (parentHeight - textView.height).coerceAtLeast(0)

                layoutParams.leftMargin = Random.nextInt(0, maxLeft)
                layoutParams.topMargin = Random.nextInt(0, maxTop)
                textView.layoutParams = layoutParams
                val delay = Random.nextLong(300, 600)
                mHandler.postDelayed(this, delay)
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
                mHandler.post(flickerRunnable)
            } else {
                Log.e("WaterMark", "playerView is not a FrameLayout")
            }
        }
    }


    private fun storeItemInPreferences(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItem(item)
    }

    private fun storeItemInPreferencesBm(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItemBm(item)
    }

    private fun playVideo(videoUrl: String, startPosition: Long = 0L, videoTittle: String, videoDesc: String) {
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
                        if (mExoPlayerFullscreen)
                            closeFullscreenDialog()
                        Log.e("videoEnded",player.toString())
//                        binding.playerView.visibility = View.GONE
                        binding.upNextOverlay.visibility = View.VISIBLE
                        binding.nextVideoTitle.text = videoTittle
                        binding.descTv.text = videoDesc?:""
                        binding.nextVideoTime.text = "1 min"
                        binding.nextProgress.setOnClickListener {
                            playNextVideo()
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
                binding.homeworkDescTv.text =  if (homeworkNames[currentVideoIndex].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkNames[currentVideoIndex]) else "NA"
                binding.homeworktittleTv.text =  if (homeworkDescs[currentVideoIndex].isNotEmpty()) " "+helperFunctions.removeBrackets(homeworkDescs[currentVideoIndex]) else "NA"
                binding.homeworkDescTv.setOnClickListener {

                    val intent = Intent(context, PdfViewActivity::class.java).apply {
                        putExtra("PDF_URL", removeBrackets(homeworkLinks[currentVideoIndex]))
                        putExtra("PDF_TITLE",homeworkNames[currentVideoIndex])
                        putExtra("FOLDER_NAME",folderName)

                    }
                    context?.startActivity(intent)
//                    helperFunctions.downloadPdfOld(requireContext(),homeworkLinks[currentVideoIndex],homeworkNames[currentVideoIndex])
                }
                binding.descTv.text = nextVideoDesc?:""
                playVideo(signedUrl,0,nextVideoTittle,nextVideoDesc)
                urlVideo = signedUrl

            } else {
                Log.e("url issues", urlVideo.toString())

            }
        }
            Log.e("currentVideoAfter",currentVideoIndex.toString())
    }


    private fun showSpeedOrQualityDialog() {
        val options = arrayOf("Speed", "Quality", "Report")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showSpeedSelectionDialog()
                    1 -> showQualityDialog()
                    3 -> showReportIssue()
                }
            }
            .show()
    }

    fun showReportIssue() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_comment, null)
        val commentEditText = dialogView.findViewById<EditText>(R.id.etComment)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Comment")
            .setView(dialogView)
            .setPositiveButton("Submit") { dialog, _ ->
                val comment = commentEditText.text.toString().trim()
                if (comment.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Comment Added: $comment", Toast.LENGTH_SHORT).show()
                    // Handle the submitted comment (e.g., save it or pass it to your backend)
                    addComment(comment)
                } else {
                    Toast.makeText(requireContext(), "Comment cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Close the dialog
            }
            .show()
    }

    private fun addComment(comment: String) {

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

    private fun changeQuality(formate:String):String{
        videourlViewModel.fetchVideoStreamUrl(courseFolderContentId, formate)
        Log.e("APIcontentId",courseFolderContentId)
        videourlViewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                urlVideo = signedUrl
            } else {
                Log.e("url issues", signedUrl.toString())
            }
        }
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
        mHandler.removeCallbacksAndMessages(null)
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
    fun removeBrackets(input: String): String {
        var url = input
        if (input.startsWith("[")){
            url = input.removePrefix("[")
        }

        if (input.endsWith("]")){
            url = url.removeSuffix("]")
        }
        return url
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
        (binding.playerApp.parent as? ViewGroup)?.removeView(binding.playerApp)
        mFullScreenDialog.addContentView(
            binding.playerApp,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mExoPlayerFullscreen = true
        mFullScreenDialog.show()
        fullScreenButton?.setImageResource(R.drawable.zoom_in_map_24)
    }

    private fun closeFullscreenDialog() {
        (binding.playerApp.parent as? ViewGroup)?.removeView(binding.playerApp)
        binding.playerRoot.addView(binding.playerApp)
        mExoPlayerFullscreen = false
        mFullScreenDialog.dismiss()
        fullScreenButton?.setImageResource(R.drawable.zoom_out_map_24)
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

    private fun initFullscreenDialog() {
        mFullScreenDialog = Dialog(requireContext(), R.style.full_screen_dialog).apply {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
        mFullScreenDialog.setOnDismissListener {
            mExoPlayerFullscreen = false
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            closeFullscreenDialog()
        }
    }

}
