package com.example.iptvmate.domain.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val number: Int,
    val group: String?,
    val epgId: String?,
    val streamUrl: String // Nueva propiedad obligatoria
)