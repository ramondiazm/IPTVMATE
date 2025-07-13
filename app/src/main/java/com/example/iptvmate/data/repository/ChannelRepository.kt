package com.example.iptvmate.data.repository

import com.example.iptvmate.domain.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

class ChannelRepository {
    suspend fun fetchChannelsFromM3U(m3uUrl: String): List<Channel> = withContext(Dispatchers.IO) {
        try {
            val channels = mutableListOf<Channel>()
            
            // Crear conexión HTTP con headers apropiados
            val url = URL(m3uUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Android TV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            connection.setRequestProperty("Accept", "*/*")
            connection.connectTimeout = 10000
            connection.readTimeout = 30000
            
            val content = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                reader.readText()
            }
            connection.disconnect()
            
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