package com.example.iptvmate.domain.model

import java.time.LocalDateTime

data class Program(
    val id: String,
    val title: String,
    val description: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val channelId: String,
    val category: String?
)

data class TimeSlot(
    val time: String,
    val hour: Int,
    val minute: Int
)

data class EPGData(
    val channels: List<Channel>,
    val programs: Map<String, List<Program>>, // channelId -> programs
    val timeSlots: List<TimeSlot>
)