package com.example.iptvmate.data.repository

import com.example.iptvmate.domain.model.Program
import com.example.iptvmate.domain.model.TimeSlot
import com.example.iptvmate.domain.model.EPGData
import com.example.iptvmate.domain.model.Channel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EPGRepository {
    
    fun generateTimeSlots(): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        for (hour in 14..23) {
            for (minute in arrayOf(0, 30)) {
                val timeString = String.format("%02d:%02d", hour, minute)
                slots.add(TimeSlot(timeString, hour, minute))
            }
        }
        return slots
    }
    
    fun generateMockPrograms(channels: List<Channel>): Map<String, List<Program>> {
        val programsMap = mutableMapOf<String, List<Program>>()
        
        val samplePrograms = listOf(
            "Al extremo",
            "Lo tomas o lo dejas",
            "La fea más bella",
            "Su vida privada",
            "Scooby-Doo y ¿quién crees tú?",
            "Sin información",
            "Asesinos",
            "Paid Programming",
            "Noticias",
            "Deportes en vivo",
            "Película de la tarde",
            "Documental",
            "Serie dramática",
            "Comedia",
            "Reality show"
        )
        
        channels.forEach { channel ->
            val programs = mutableListOf<Program>()
            val timeSlots = generateTimeSlots()
            
            timeSlots.forEachIndexed { index, timeSlot ->
                val startTime = LocalDateTime.now()
                    .withHour(timeSlot.hour)
                    .withMinute(timeSlot.minute)
                    .withSecond(0)
                    .withNano(0)
                
                val endTime = if (index < timeSlots.size - 1) {
                    val nextSlot = timeSlots[index + 1]
                    startTime.withHour(nextSlot.hour).withMinute(nextSlot.minute)
                } else {
                    startTime.plusMinutes(30)
                }
                
                val programTitle = samplePrograms.random()
                
                programs.add(
                    Program(
                        id = "${channel.id}_${timeSlot.time}",
                        title = programTitle,
                        description = "Descripción del programa $programTitle en ${channel.name}",
                        startTime = startTime,
                        endTime = endTime,
                        channelId = channel.id,
                        category = channel.group
                    )
                )
            }
            
            programsMap[channel.id] = programs
        }
        
        return programsMap
    }
    
    fun getEPGData(channels: List<Channel>): EPGData {
        return EPGData(
            channels = channels,
            programs = generateMockPrograms(channels),
            timeSlots = generateTimeSlots()
        )
    }
    
    fun getCurrentProgram(channelId: String, programs: Map<String, List<Program>>): Program? {
        val channelPrograms = programs[channelId] ?: return null
        val now = LocalDateTime.now()
        
        return channelPrograms.find { program ->
            now.isAfter(program.startTime) && now.isBefore(program.endTime)
        } ?: channelPrograms.firstOrNull()
    }
}