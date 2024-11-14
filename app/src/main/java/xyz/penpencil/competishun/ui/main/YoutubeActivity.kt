package xyz.penpencil.competishun.ui.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import xyz.penpencil.competishun.R

class YoutubeActivity : AppCompatActivity() {
    private var isInFullscreen = false
    private var youTubePlayerInstance: YouTubePlayer? = null

    private lateinit var backButton : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)

        backButton  = findViewById(R.id.iv_videoBackPressed)

        backButton.setOnClickListener {
            if (isInFullscreen) {
                exitFullScreenMode()
            } else {
                super.onBackPressed()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fullscreenButton: ImageView = findViewById(R.id.fullscreenButton)

        val videoUrl = intent.getStringExtra("url")
        val videoName = intent.getStringExtra("urlDescription")
        val videoId = extractYouTubeId(videoUrl.toString())
        val youtubePlayerView: YouTubePlayerView = findViewById(R.id.youtubePlayerView)

        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayerInstance = youTubePlayer // Save the YouTubePlayer instance
                videoId?.let {
                    youTubePlayer.cueVideo(it, 0f) // Cue the video in paused mode
                }
            }
        })

        // Add custom fullscreen control by adding a button or capturing fullscreen events
        youtubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                enterFullScreenMode()
            }

            override fun onYouTubePlayerExitFullScreen() {
                exitFullScreenMode()
            }
        })

        fullscreenButton.setOnClickListener {
            if (isInFullscreen) {
                exitFullScreenMode()
            } else {
                enterFullScreenMode()
                youTubePlayerInstance?.play()
            }
        }
    }


    override fun onBackPressed() {
        if (isInFullscreen) {
            exitFullScreenMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun enterFullScreenMode() {
        isInFullscreen = true
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun exitFullScreenMode() {
        isInFullscreen = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isInFullscreen) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val youtubePlayerView: YouTubePlayerView = findViewById(R.id.youtubePlayerView)
        youtubePlayerView.release()
    }

    private fun extractYouTubeId(url: String): String? {
        val pattern = "(?:youtu\\.be/|youtube\\.com/(?:watch\\?v=|embed/|v/|.+\\?v=))([\\w-]{11})"
        val regex = Regex(pattern)
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }
}
