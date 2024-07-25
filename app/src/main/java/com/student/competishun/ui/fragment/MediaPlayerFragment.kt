package com.student.competishun.ui.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.student.competishun.databinding.FragmentMediaPlayerBinding

class MediaPlayerFragment : Fragment() {

    private lateinit var binding: FragmentMediaPlayerBinding
    private lateinit var player: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString("url")

        //initialize exoplayer
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerview.player = player

        val mediaItem = MediaItem.fromUri(url!!)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        ViewCompat.setOnApplyWindowInsetsListener(binding.playerview) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
        // Reset the screen orientation to the user's preference
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
