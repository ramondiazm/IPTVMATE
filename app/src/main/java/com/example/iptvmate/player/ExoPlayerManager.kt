package com.example.iptvmate.player

import android.content.Context
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.iptvmate.domain.model.Channel

@UnstableApi
class ExoPlayerManager : PlayerManager {
    private var exoPlayer: ExoPlayer? = null
    private var currentChannelId: String? = null
    private var lastSurface: SurfaceView? = null

    override fun play(channel: Channel, context: Context, surfaceView: SurfaceView) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
        if (lastSurface != surfaceView) {
            lastSurface?.holder?.removeCallback(surfaceCallback)
            surfaceView.holder.addCallback(surfaceCallback)
            exoPlayer?.setVideoSurfaceView(surfaceView)
            lastSurface = surfaceView
        }
        if (currentChannelId != channel.id) {
            val mediaSource = buildMediaSource(channel, context)
            exoPlayer?.setMediaSource(mediaSource)
            exoPlayer?.prepare()
            exoPlayer?.playWhenReady = true
            currentChannelId = channel.id
        }
    }

    override fun stop() {
        exoPlayer?.stop()
    }

    private fun buildMediaSource(channel: Channel, context: Context): MediaSource {
        val url = channel.streamUrl // Usar la URL real del stream
        val uri = Uri.parse(url)
        val dataSourceFactory = DefaultDataSource.Factory(context)
        return when {
            url.endsWith(".m3u8", true) -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            url.endsWith(".mpd", true) -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            url.startsWith("udp://", true) -> {
                // UDP multicast via RTSP extension
                RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(uri))
            }
            url.endsWith(".mp4", true) -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            else -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
        }
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            exoPlayer?.setVideoSurfaceHolder(holder)
        }
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            exoPlayer?.setVideoSurfaceHolder(null)
        }
    }
}