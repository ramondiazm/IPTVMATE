package com.example.iptvmate.ui.theme

import androidx.compose.ui.graphics.Color

// TiViMate Color Scheme - Fieles al original
val TiViMateBackground = Color(0xFF1A1A1A)        // Fondo principal gris oscuro
val TiViMateSidebar = Color(0xFF2A2A2A)           // Sidebar gris medio
val TiViMatePanel = Color(0xFF2A2A2A)             // Paneles gris medio
val TiViMateSelected = Color(0xFF404040)          // Elemento seleccionado gris
val TiViMateFocus = Color(0xFF606060)             // Foco gris claro (sin azul)
val TiViMateText = Color(0xFFFFFFFF)              // Texto principal blanco
val TiViMateTextSecondary = Color(0xFFB0B0B0)     // Texto secundario gris claro
val TiViMateAccent = Color(0xFF909090)            // Acento gris (sin azul)
val TiViMateLive = Color(0xFFFF4444)              // Rojo para "EN VIVO"
val TiViMateOverlay = Color(0x80000000)           // Overlay semi-transparente

// Legacy colors (mantener compatibilidad)
val Purple80 = TiViMateText
val PurpleGrey80 = TiViMateTextSecondary
val Pink80 = TiViMateLive

val Purple40 = TiViMateSelected
val PurpleGrey40 = TiViMatePanel
val Pink40 = TiViMateLive