package com.example.iptvmate.player

import android.content.Context
import android.view.SurfaceView
import com.example.iptvmate.domain.model.Channel

interface PlayerManager {
    fun play(channel: Channel, context: Context, surfaceView: SurfaceView)
    fun stop()
    // Implemented by ExoPlayerManager for IPTV playback (UDP, HLS, DASH, HTTP, MP4)
}