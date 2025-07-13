package com.example.iptvmate.data.repository

import com.example.iptvmate.domain.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ChannelRepository {
    suspend fun fetchChannelsFromM3U(m3uUrl: String): List<Channel> = withContext(Dispatchers.IO) {
        try {
            val channels = mutableListOf<Channel>()
            val content = URL(m3uUrl).readText()
            val lines = content.lines()
            
            var currentName = ""
            var currentLogo: String? = null
            var currentGroup: String? = null
            var currentEpgId: String? = null
            var number = 1
            
            for (i in lines.indices) {
                val line = lines[i].trim()
                if (line.startsWith("#EXTINF")) {
                    // Parsing mejorado que maneja atributos con y sin comillas
                    currentName = extractAttribute(line, "tvg-name") ?: line.substringAfter(",").trim()
                    currentLogo = extractAttribute(line, "tvg-logo")
                    currentGroup = extractAttribute(line, "group-title")
                    currentEpgId = extractAttribute(line, "tvg-ID") ?: extractAttribute(line, "tvg-id")
                } else if (line.isNotBlank() && !line.startsWith("#")) {
                    // Es la URL del stream
                    if (currentName.isNotEmpty()) {
                        channels.add(
                            Channel(
                                id = "$currentName$number",
                                name = currentName,
                                logoUrl = currentLogo,
                                number = number++,
                                group = currentGroup,
                                epgId = currentEpgId,
                                streamUrl = line,
                                isFavorite = false
                            )
                        )
                    }
                    // Reset para el siguiente canal
                    currentName = ""
                    currentLogo = null
                    currentGroup = null
                    currentEpgId = null
                }
            }
            
            println("ChannelRepository: Parsed ${channels.size} channels")
            channels
        } catch (e: Exception) {
            println("ChannelRepository Error: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    private fun extractAttribute(line: String, attributeName: String): String? {
        // Busca atributos con comillas dobles
        val quotedRegex = Regex("$attributeName=\"([^\"]*)\"", RegexOption.IGNORE_CASE)
        quotedRegex.find(line)?.let { return it.groupValues[1] }
        
        // Busca atributos sin comillas (hasta el siguiente espacio o final de línea)
        val unquotedRegex = Regex("$attributeName=([^\\s,]+)", RegexOption.IGNORE_CASE)
        unquotedRegex.find(line)?.let { return it.groupValues[1] }
        
        return null
    }
}