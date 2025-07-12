package com.example.iptvmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.iptvmate.domain.model.Channel

@Composable
fun ChannelList(
    channels: List<Channel>,
    selectedChannel: Channel?,
    onChannelSelected: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxHeight().width(320.dp).background(Color.DarkGray)) {
        items(channels) { channel ->
            val isSelected = channel.id == selectedChannel?.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onChannelSelected(channel) }
                    .padding(12.dp)
            ) {
                // TODO: Add channel logo if available
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material3.Text(
                    text = channel.name,
                    color = if (isSelected) Color.White else Color.LightGray
                )
            }
        }
    }
}