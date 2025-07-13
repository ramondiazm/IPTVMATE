package com.example.iptvmate.data.repository

import com.example.iptvmate.domain.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ChannelRepository {
    suspend fun fetchChannelsFromM3U(m3uUrl: String): List<Channel> = withContext(Dispatchers.IO) {
        val channels = mutableListOf<Channel>()
        val lines = URL(m3uUrl).readText().lines()
        var currentName = ""
        var currentLogo: String? = null
        var currentGroup: String? = null
        var currentEpgId: String? = null
        var number = 1
        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXTINF")) {
                val nameMatch = Regex("tvg-name=\\\"(.*?)\\\"").find(line)
                val logoMatch = Regex("tvg-logo=([\"`'])(.*?)\\1").find(line) // Soporta comillas dobles, simples o invertidas
                val groupMatch = Regex("group-title=([\"`'])(.*?)\\1").find(line)
                val epgIdMatch = Regex("tvg-id=([\"`'])(.*?)\\1").find(line)
                currentName = nameMatch?.groupValues?.get(1) ?: line.substringAfter(",").trim()
                currentLogo = logoMatch?.groupValues?.get(2)
                currentGroup = groupMatch?.groupValues?.get(2)
                currentEpgId = epgIdMatch?.groupValues?.get(2)
            } else if (line.isNotBlank() && !line.startsWith("#")) {
                // Es la URL del stream
                channels.add(
                    Channel(
                        id = currentName + number,
                        name = currentName,
                        logoUrl = currentLogo,
                        number = number++,
                        group = currentGroup,
                        epgId = currentEpgId,
                        streamUrl = line.trim(),
                        isFavorite = false // Por defecto no es favorito
                    )
                )
            }
        }
        channels
    }
}