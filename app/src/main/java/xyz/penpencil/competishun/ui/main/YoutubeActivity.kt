package xyz.penpencil.competishun.ui.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import xyz.penpencil.competishun.R

class YoutubeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_youtube)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val videoUrl = intent.getStringExtra("url")
//        val videoUrl = "https://www.youtube.com/watch?v=06dJv3gJX88"
        val videoId = extractYouTubeId(videoUrl.toString())
        val youtubePlayerView: YouTubePlayerView = findViewById(R.id.youtubePlayerView)

        lifecycle.addObserver(youtubePlayerView)
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoId?.let {
                    youTubePlayer.loadVideo(it, 0f)
                }
            }
        })

    }

    private fun extractYouTubeVideoId(videoUrl: String?): String? {
        return videoUrl?.substringAfter("v=")
    }

    fun extractYouTubeId(url: String): String? {
        val pattern = "(?:youtu\\.be/|youtube\\.com/(?:watch\\?v=|embed/|v/|.+\\?v=))([\\w-]{11})"
        val regex = Regex(pattern)
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val youtubePlayerView: YouTubePlayerView = findViewById(R.id.youtubePlayerView)
        youtubePlayerView.release()
    }
}