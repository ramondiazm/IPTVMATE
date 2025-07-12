package com.example.iptvmate.ui.components

import android.content.Context
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import com.example.iptvmate.domain.model.Channel
import com.example.iptvmate.player.PlayerManager
import com.example.iptvmate.player.ExoPlayerManager

@OptIn(UnstableApi::class)
@Composable
fun PlayerPreview(
    channel: Channel?,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerManager = remember { ExoPlayerManager() as PlayerManager }
    var surfaceView by remember { mutableStateOf<SurfaceView?>(null) }
    val previewHeight by animateDpAsState(if (isFullscreen) 400.dp else 120.dp)
    val previewWidth by animateDpAsState(if (isFullscreen) 700.dp else 220.dp)

    Box(
        modifier = modifier
            .size(width = previewWidth, height = previewHeight)
            .background(Color.Black)
            .clickable { onToggleFullscreen() }
    ) {
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).also {
                    surfaceView = it
                    channel?.let { ch ->
                        playerManager.play(ch, ctx, it)
                    }
                }
            },
            update = { view ->
                if (channel != null && surfaceView != null) {
                    playerManager.play(channel, context, view)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}