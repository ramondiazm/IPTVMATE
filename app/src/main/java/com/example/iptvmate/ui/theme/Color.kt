package com.example.iptvmate.ui.theme

import androidx.compose.ui.graphics.Color

// TiViMate Color Scheme
val TiViMateBackground = Color(0xFF1A1A1A)        // Fondo principal gris oscuro
val TiViMateSidebar = Color(0xFF2A2A2A)           // Sidebar gris medio
val TiViMatePanel = Color(0xFF333333)             // Paneles gris
val TiViMateSelected = Color(0xFF4A4A4A)          // Elemento seleccionado gris claro
val TiViMateFocus = Color(0xFFFFFFFF)             // Foco blanco
val TiViMateText = Color(0xFFFFFFFF)              // Texto principal blanco
val TiViMateTextSecondary = Color(0xFFB0B0B0)     // Texto secundario gris claro
val TiViMateAccent = Color(0xFF00A8FF)            // Acento azul suave (solo para progress)
val TiViMateLive = Color(0xFFFF4444)              // Rojo para "EN VIVO"
val TiViMateOverlay = Color(0x80000000)           // Overlay semi-transparente

// Legacy colors (mantener compatibilidad)
val Purple80 = TiViMateText
val PurpleGrey80 = TiViMateTextSecondary
val Pink80 = TiViMateLive

val Purple40 = TiViMateSelected
val PurpleGrey40 = TiViMatePanel
val Pink40 = TiViMateLive