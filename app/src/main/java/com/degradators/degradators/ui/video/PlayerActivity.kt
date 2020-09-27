package com.degradators.degradators.ui.video

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.degradators.degradators.R
import com.degradators.degradators.common.adapter.VIDEO_URL
import com.degradators.degradators.databinding.ActivityVideoPlayerBinding
import com.degradators.degradators.ui.main.BaseActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerActivity : BaseActivity<PlayerViewModel>() {
    	private lateinit var binding: ActivityVideoPlayerBinding
    private var player: SimpleExoPlayer? = null
    private var playerStateData: PlayerData? = null

    override val viewModel by viewModels<PlayerViewModel> { factory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player)
        binding.apply {
            playerViewModel = viewModel
            lifecycleOwner = this@PlayerActivity
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }

        toggleNavigationAndStatusBarVisibility()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }

        toggleNavigationAndStatusBarVisibility()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build().apply {
//				binding.playerView.isVisible = true
                binding.playerView.player = this
                playWhenReady = true
            }
        }

        val dataSourceFactory = this.let { context ->
            DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.applicationInfo.packageName)
            )
        }

        val url = intent.extras?.get(VIDEO_URL) as String

        val uri = Uri.parse(url)
        val type = Util.inferContentType(uri)

        val mediaSource = when (type) {
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .setDrmSessionManager(DrmSessionManager.DUMMY)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type $type")
        }

        player?.prepare(mediaSource)

        playerStateData?.let {
            player?.seekTo(it.contentPosition)
        }
    }

    private fun releasePlayer() {
        player?.apply {
//            binding.playerView.isVisible = false
            playerStateData = PlayerData(contentPosition = contentPosition)
            release()
        }

        player = null
    }

    private fun toggleNavigationAndStatusBarVisibility() {
        val uiOptions = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = (uiOptions ?: 0) xor
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE xor
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION xor
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN xor
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY xor
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION xor
                View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private data class PlayerData(val contentPosition: Long)
}