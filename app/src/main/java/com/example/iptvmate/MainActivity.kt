package com.example.iptvmate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.example.iptvmate.domain.model.Channel
import com.example.iptvmate.domain.model.Program
import com.example.iptvmate.domain.model.EPGData
import com.example.iptvmate.domain.model.TimeSlot
import com.example.iptvmate.ui.components.ExoPlayerView
import com.example.iptvmate.ui.theme.IPTVMATETheme
import com.example.iptvmate.data.repository.ChannelRepository
import com.example.iptvmate.data.repository.EPGRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IPTVMATETheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    IPTVMainScreen()
                }
            }
        }
    }
}

@Composable
fun TiviMateLayout(
    channels: List<Channel>,
    epgData: EPGData?,
    selectedChannel: Channel?,
    selectedProgram: Program?,
    categories: List<String>,
    menuItems: List<String>,
    selectedCategory: String,
    selectedMenuIndex: Int,
    selectedCategoryIndex: Int,
    selectedChannelIndex: Int,
    focusedArea: String,
    onChannelSelected: (Channel) -> Unit,
    onCategorySelected: (String) -> Unit,
    onMenuSelected: (Int) -> Unit,
    onChannelIndexChanged: (Int) -> Unit,
    onFocusChanged: (String) -> Unit
) {
    // Estados para controlar el plegado de menús con lógica mejorada
    val sidebarCollapsed = focusedArea in listOf("channels", "epg")
    val categoriesCollapsed = focusedArea == "epg"
    val sidebarHidden = focusedArea == "epg" && categoriesCollapsed
    
    // Animaciones para el ancho de los menús
    val sidebarWidth by animateDpAsState(
        targetValue = when {
            sidebarHidden -> 0.dp
            sidebarCollapsed -> 60.dp
            else -> 280.dp
        },
        animationSpec = tween(300)
    )
    val categoriesWidth by animateDpAsState(
        targetValue = if (categoriesCollapsed) 0.dp else 300.dp,
        animationSpec = tween(300)
    )
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1419))
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionLeft -> {
                            when (focusedArea) {
                                "categories" -> onFocusChanged("menu")
                                "channels" -> onFocusChanged("categories")
                                "epg" -> onFocusChanged("channels")
                            }
                            true
                        }
                        Key.DirectionRight -> {
                            when (focusedArea) {
                                "menu" -> onFocusChanged("categories")
                                "categories" -> onFocusChanged("channels")
                                "channels" -> onFocusChanged("epg")
                            }
                            true
                        }
                        Key.DirectionUp -> {
                            when (focusedArea) {
                                "menu" -> {
                                    val newIndex = (selectedMenuIndex - 1).coerceAtLeast(0)
                                    onMenuSelected(newIndex)
                                }
                                "categories" -> {
                                    val newIndex = (selectedCategoryIndex - 1).coerceAtLeast(0)
                                    if (newIndex < categories.size) {
                                        onCategorySelected(categories[newIndex])
                                    }
                                }
                                "channels" -> {
                                    val newIndex = (selectedChannelIndex - 1).coerceAtLeast(0)
                                    if (newIndex < channels.size) {
                                        onChannelIndexChanged(newIndex)
                                        onChannelSelected(channels[newIndex])
                                    }
                                }
                            }
                            true
                        }
                        Key.DirectionDown -> {
                            when (focusedArea) {
                                "menu" -> {
                                    val newIndex = (selectedMenuIndex + 1).coerceAtMost(menuItems.size - 1)
                                    onMenuSelected(newIndex)
                                }
                                "categories" -> {
                                    val newIndex = (selectedCategoryIndex + 1).coerceAtMost(categories.size - 1)
                                    if (newIndex < categories.size) {
                                        onCategorySelected(categories[newIndex])
                                    }
                                }
                                "channels" -> {
                                    val newIndex = (selectedChannelIndex + 1).coerceAtMost(channels.size - 1)
                                    if (newIndex < channels.size) {
                                        onChannelIndexChanged(newIndex)
                                        onChannelSelected(channels[newIndex])
                                    }
                                }
                            }
                            true
                        }
                        Key.Enter, Key.DirectionCenter -> {
                            // Expandir a fullscreen cuando se presiona OK en un canal
                            if (focusedArea == "channels" && selectedChannel != null) {
                                // TODO: Implementar fullscreen player
                                true
                            } else false
                        }
                        else -> false
                    }
                } else false
            }
            .focusable()
    ) {
        // Sidebar izquierdo
        if (sidebarWidth > 0.dp) {
            SidebarMenu(
                menuItems = menuItems,
                selectedIndex = selectedMenuIndex,
                isFocused = focusedArea == "menu",
                isCollapsed = sidebarCollapsed,
                onItemSelected = onMenuSelected,
                onFocusChanged = { onFocusChanged("menu") },
                modifier = Modifier.width(sidebarWidth)
            )
        }
        
        // Panel central de categorías
        if (categoriesWidth > 0.dp) {
            CategoriesPanel(
                categories = categories,
                selectedCategory = selectedCategory,
                selectedIndex = selectedCategoryIndex,
                isFocused = focusedArea == "categories",
                onCategorySelected = onCategorySelected,
                onFocusChanged = { onFocusChanged("categories") },
                modifier = Modifier.width(categoriesWidth)
            )
        }
        
        // Área principal con EPG y mini player
        MainContentArea(
            channels = channels,
            epgData = epgData,
            selectedChannel = selectedChannel,
            selectedProgram = selectedProgram,
            selectedChannelIndex = selectedChannelIndex,
            isFocused = focusedArea == "channels" || focusedArea == "epg",
            focusedArea = focusedArea,
            onChannelSelected = onChannelSelected,
            onChannelIndexChanged = { index ->
                // Actualizar el índice del canal seleccionado
                if (index < channels.size) {
                    onChannelSelected(channels[index])
                }
            },
            onFocusChanged = onFocusChanged
        )
    }
}

@Composable
fun SidebarMenu(
    menuItems: List<String>,
    selectedIndex: Int,
    isFocused: Boolean,
    isCollapsed: Boolean,
    onItemSelected: (Int) -> Unit,
    onFocusChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF16213E))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(if (isCollapsed) 8.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Logo TiviMate (solo cuando no está colapsado)
            if (!isCollapsed) {
                androidx.compose.material3.Text(
                    text = "tivimate",
                    color = Color(0xFF00BCD4),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
            
            // Opciones del menú
            menuItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex && index == 1 // TV está seleccionado
                val isBuscar = index == 0
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            when {
                                isBuscar -> Color(0xFF0F3460)
                                isSelected -> Color(0xFF4FC3F7)
                                else -> Color.Transparent
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onItemSelected(index) }
                        .focusable()
                        .onFocusChanged { if (it.isFocused) onFocusChanged() }
                        .padding(if (isCollapsed) 8.dp else 16.dp),
                    contentAlignment = if (isCollapsed) Alignment.Center else Alignment.CenterStart
                ) {
                    if (isCollapsed) {
                        // Solo mostrar emoji cuando está colapsado
                        androidx.compose.material3.Text(
                            text = item.take(2), // Solo el emoji
                            color = when {
                                isBuscar -> Color.White.copy(alpha = 0.7f)
                                isSelected -> Color.Black
                                else -> Color.White.copy(alpha = 0.8f)
                            },
                            fontSize = 20.sp
                        )
                    } else {
                        androidx.compose.material3.Text(
                            text = item,
                            color = when {
                                isBuscar -> Color.White.copy(alpha = 0.7f)
                                isSelected -> Color.Black
                                else -> Color.White.copy(alpha = 0.8f)
                            },
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesPanel(
    categories: List<String>,
    selectedCategory: String,
    selectedIndex: Int,
    isFocused: Boolean,
    onCategorySelected: (String) -> Unit,
    onFocusChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(categories) { index, category ->
                val isSelected = category == selectedCategory
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) Color(0xFF0F3460) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onCategorySelected(category) }
                        .focusable()
                        .onFocusChanged { if (it.isFocused) onFocusChanged() }
                        .padding(16.dp)
                ) {
                    androidx.compose.material3.Text(
                        text = category,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun MainContentArea(
    channels: List<Channel>,
    epgData: EPGData?,
    selectedChannel: Channel?,
    selectedProgram: Program?,
    selectedChannelIndex: Int,
    isFocused: Boolean,
    focusedArea: String,
    onChannelSelected: (Channel) -> Unit,
    onFocusChanged: (String) -> Unit,
    onChannelIndexChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1419))
    ) {
        // Área izquierda con mini player compacto e información del programa
        Column(
            modifier = Modifier
                .width(400.dp)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Mini player compacto (alineado horizontalmente)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                selectedChannel?.let { channel ->
                    ExoPlayerView(
                        streamUrl = channel.streamUrl,
                        isPlaying = true,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Overlay con información del canal
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                Color.Black.copy(alpha = 0.7f),
                                RoundedCornerShape(topEnd = 8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            androidx.compose.material3.Text(
                                text = channel.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            androidx.compose.material3.Text(
                                text = "EN VIVO",
                                color = Color.Red,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } ?: run {
                    // Mostrar mensaje "Sin señal" cuando no hay canal
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Text(
                            text = "Sin señal",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información del programa (debajo del player)
            selectedProgram?.let { program ->
                ProgramInfoPanel(
                    program = program,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // EPG Grid y lista de canales (lado derecho)
        epgData?.let { epg ->
            NavigableEPGGrid(
                channels = channels,
                epgData = epg,
                selectedChannel = selectedChannel,
                selectedChannelIndex = selectedChannelIndex,
                isFocused = focusedArea == "channels" || focusedArea == "epg",
                focusedArea = focusedArea,
                onChannelSelected = onChannelSelected,
                onChannelIndexChanged = onChannelIndexChanged,
                onFocusChanged = onFocusChanged,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ProgramInfoPanel(
    program: Program,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                Color(0xFF16213E),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Título del programa
        androidx.compose.material3.Text(
            text = program.title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        // Horario
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        androidx.compose.material3.Text(
            text = "${program.startTime.format(timeFormatter)} — ${program.endTime.format(timeFormatter)}",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Barra de progreso
        val now = LocalDateTime.now()
        val totalDuration = java.time.Duration.between(program.startTime, program.endTime).toMinutes()
        val elapsed = if (now.isAfter(program.startTime) && now.isBefore(program.endTime)) {
            java.time.Duration.between(program.startTime, now).toMinutes()
        } else 0
        
        val progress = if (totalDuration > 0) elapsed.toFloat() / totalDuration.toFloat() else 0f
        
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF4FC3F7),
            trackColor = Color.White.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Descripción
        program.description?.let { description ->
            androidx.compose.material3.Text(
                text = description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NavigableEPGGrid(
    channels: List<Channel>,
    epgData: EPGData,
    selectedChannel: Channel?,
    selectedChannelIndex: Int,
    isFocused: Boolean,
    focusedArea: String,
    onChannelSelected: (Channel) -> Unit,
    onChannelIndexChanged: (Int) -> Unit,
    onFocusChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProgramIndex by remember { mutableStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(isFocused) {
        if (isFocused) {
            focusRequester.requestFocus()
        }
    }
    
    Column(
        modifier = modifier
            .padding(16.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionUp -> {
                            if (selectedChannelIndex > 0) {
                                val newIndex = selectedChannelIndex - 1
                                onChannelIndexChanged(newIndex)
                                onChannelSelected(channels[newIndex])
                            }
                            true
                        }
                        Key.DirectionDown -> {
                            if (selectedChannelIndex < channels.size - 1) {
                                val newIndex = selectedChannelIndex + 1
                                onChannelIndexChanged(newIndex)
                                onChannelSelected(channels[newIndex])
                            }
                            true
                        }
                        Key.DirectionLeft -> {
                            if (focusedArea == "epg" && selectedProgramIndex > 0) {
                                selectedProgramIndex--
                            } else {
                                onFocusChanged("categories")
                            }
                            true
                        }
                        Key.DirectionRight -> {
                            if (focusedArea == "channels") {
                                onFocusChanged("epg")
                            } else if (focusedArea == "epg") {
                                val currentChannel = channels.getOrNull(selectedChannelIndex)
                                val programs = currentChannel?.let { epgData.programs[it.id] } ?: emptyList()
                                if (selectedProgramIndex < programs.size - 1) {
                                    selectedProgramIndex++
                                }
                            }
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        // Header con horarios
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // Espacio para números de canal
            Spacer(modifier = Modifier.width(80.dp))
            
            // Horarios
            LazyRow {
                items(epgData.timeSlots) { timeSlot ->
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Text(
                            text = timeSlot.time,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Grid de canales y programas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(channels) { index, channel ->
                val isChannelSelected = index == selectedChannelIndex
                val channelPrograms = epgData.programs[channel.id] ?: emptyList()
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            if (isChannelSelected) Color(0xFF0F3460) else Color(0xFF16213E),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Número y logo del canal
                    Row(
                        modifier = Modifier.width(80.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(
                            text = channel.number.toString(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(30.dp)
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Gray, CircleShape)
                        )
                    }
                    
                    // Programas en grid horizontal
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(channelPrograms) { programIndex, program ->
                            val isProgramSelected = isChannelSelected && programIndex == selectedProgramIndex
                            ProgramCell(
                                program = program,
                                isChannelSelected = isChannelSelected,
                                isProgramSelected = isProgramSelected,
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgramCell(
    program: Program,
    isChannelSelected: Boolean,
    isProgramSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now()
    val isCurrentProgram = now.isAfter(program.startTime) && now.isBefore(program.endTime)
    
    val backgroundColor = when {
        isProgramSelected -> Color(0xFF3B82F6) // Azul brillante para programa seleccionado
        isCurrentProgram && isChannelSelected -> Color(0xFF4FC3F7)
        isCurrentProgram -> Color(0xFF2196F3)
        isChannelSelected -> Color(0xFF0F3460)
        else -> Color(0xFF1A1A2E)
    }
    
    val borderColor = if (isProgramSelected) Color.White else Color.Transparent
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                backgroundColor,
                RoundedCornerShape(4.dp)
            )
            .border(
                width = if (isProgramSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            androidx.compose.material3.Text(
                text = program.title,
                color = if (isCurrentProgram && isChannelSelected) Color.Black else Color.White,
                fontSize = 12.sp,
                fontWeight = if (isProgramSelected || isCurrentProgram) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            androidx.compose.material3.Text(
                text = program.startTime.format(timeFormatter),
                color = if (isCurrentProgram && isChannelSelected) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun IPTVMainScreen() {
    val context = LocalContext.current
    val channelRepository = remember { ChannelRepository() }
    val epgRepository = remember { EPGRepository() }
    
    var channels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    var epgData by remember { mutableStateOf<EPGData?>(null) }
    var selectedChannel by remember { mutableStateOf<Channel?>(null) }
    var selectedProgram by remember { mutableStateOf<Program?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("Todos los canales") }
    var selectedMenuIndex by remember { mutableStateOf(0) }
    var selectedCategoryIndex by remember { mutableStateOf(1) }
    var selectedChannelIndex by remember { mutableStateOf(0) }
    var focusedArea by remember { mutableStateOf("menu") } // menu, categories, channels, epg
    
    val m3uUrl = "https://opop.pro/XLE8sWYgsUXvNp"
    
    // Categorías disponibles
    val categories = listOf(
        "Favoritos",
        "Todos los canales", 
        "TV NACIONAL",
        "ENTRETENIMIENTO",
        "CINE",
        "CULTURA",
        "NIÑOS",
        "MÚSICA",
        "DEPORTES",
        "RELIGIÓN"
    )
    
    // Opciones del menú lateral
    val menuItems = listOf(
        "🔍 Buscar",
        "📺 TV",
        "📹 Grabaciones", 
        "📋 Mi lista",
        "⚙️ Opciones"
    )
    
    LaunchedEffect(Unit) {
        try {
            Log.d("IPTV", "Iniciando carga de canales desde: $m3uUrl")
            val loaded = channelRepository.fetchChannelsFromM3U(m3uUrl)
            Log.d("IPTV", "Canales cargados: ${loaded.size}")
            channels = loaded
            selectedChannel = loaded.firstOrNull()
            
            // Generar datos EPG
            val epg = epgRepository.getEPGData(loaded)
            epgData = epg
            
            // Seleccionar programa actual
            selectedChannel?.let { channel ->
                selectedProgram = epgRepository.getCurrentProgram(channel.id, epg.programs)
            }
            
            Log.d("IPTV", "Canal seleccionado: ${selectedChannel?.name}")
            Log.d("IPTV", "Programa actual: ${selectedProgram?.title}")
            isLoading = false
        } catch (e: Exception) {
            Log.e("IPTV", "Error al cargar canales: ${e.message}", e)
            errorMessage = e.message ?: e.toString()
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = "Cargando canales...",
                    color = Color.Yellow,
                    fontSize = 18.sp
                )
            }
        }
        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }
        }
        channels.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = "No se pudieron cargar canales.",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        else -> {
            TiviMateLayout(
                channels = channels,
                epgData = epgData,
                selectedChannel = selectedChannel,
                selectedProgram = selectedProgram,
                categories = categories,
                menuItems = menuItems,
                selectedCategory = selectedCategory,
                selectedMenuIndex = selectedMenuIndex,
                selectedCategoryIndex = selectedCategoryIndex,
                selectedChannelIndex = selectedChannelIndex,
                focusedArea = focusedArea,
                onChannelSelected = { channel ->
                    selectedChannel = channel
                    epgData?.let { epg ->
                        selectedProgram = epgRepository.getCurrentProgram(channel.id, epg.programs)
                    }
                },
                onCategorySelected = { category ->
                    selectedCategory = category
                },
                onMenuSelected = { index ->
                    selectedMenuIndex = index
                },
                onChannelIndexChanged = { index ->
                    selectedChannelIndex = index
                },
                onFocusChanged = { area ->
                    focusedArea = area
                }
            )
        }
    }
}