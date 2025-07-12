# IPTV Mate - Professional Android TV Application

🚀 **Una aplicación IPTV moderna y profesional para Android TV inspirada en TiViMate**

## 📱 Características Principales

### 🎯 **Interfaz de Usuario**
- **Diseño TiViMate-like**: Interfaz profesional con sidebar, categorías, grid de canales y EPG
- **Mini-player compacto**: Preview en vivo con audio y video del canal seleccionado
- **Menús plegables**: Animaciones fluidas para colapsar sidebar y categorías
- **Navegación DPAD completa**: Optimizado para control remoto de Android TV

### 🎮 **Navegación y UX**
- **Foco visible**: Indicadores claros de selección en todos los elementos
- **Transiciones suaves**: Animaciones sin flashes ni cortes
- **Estados de colapso**: Progresivo (íconos → categorías → solo EPG)
- **Navegación intuitiva**: Flujo natural entre menú, categorías, canales y EPG

### 📺 **Reproducción de Video**
- **ExoPlayer integrado**: Player robusto con gestión completa de lifecycle
- **Soporte multi-formato**: UDP (multicast/unicast), HLS, HTTP MPEG-TS
- **Preview en tiempo real**: Cambio automático al seleccionar canales
- **Manejo de errores**: Mensaje "Sin señal" para fuentes no válidas

### 📊 **Datos IPTV**
- **M3U real**: Integración con https://opop.pro/XLE8sWYgsUXvNp
- **EPG (XMLTV)**: Guía de programación desde https://opop.pro/22AWtsbCszVyW
- **Parsing optimizado**: Procesamiento en background sin bloquear UI
- **Información completa**: Título, horario, progreso y descripción de programas

## 🏗️ Arquitectura Técnica

### **Stack Tecnológico**
- **Kotlin**: 100% Kotlin con Java 17
- **Jetpack Compose for TV**: UI moderna y declarativa
- **ExoPlayer**: Backend de reproducción (preparado para LibVLC)
- **Koin**: Inyección de dependencias
- **Coroutines**: Programación asíncrona

### **Estructura del Proyecto**
```
app/src/main/java/com/example/iptvmate/
├── MainActivity.kt              # UI principal y navegación
├── data/
│   ├── model/                   # Modelos de datos
│   ├── parser/                  # Parsers M3U y XMLTV
│   └── repository/              # Repositorios de datos
├── domain/
│   ├── model/                   # Entidades de dominio
│   └── usecase/                 # Casos de uso
├── player/
│   ├── ExoPlayerManager.kt      # Gestión de ExoPlayer
│   └── PlayerManager.kt         # Interfaz de player
├── ui/
│   ├── components/              # Componentes reutilizables
│   └── theme/                   # Tema y estilos
└── di/
    └── AppModule.kt             # Configuración DI
```

### **Componentes Principales**

#### 🎛️ **TiviMateLayout**
- Composable principal que maneja toda la navegación DPAD
- Estados reactivos para colapso de menús
- Animaciones fluidas con `animateDpAsState`

#### 📺 **MainContentArea**
- Layout optimizado con mini-player y EPG
- Gestión de estados de reproducción
- Información de programas en tiempo real

#### 🎮 **NavigableEPGGrid**
- Grid navegable de canales y programas
- Selección individual de programas
- Integración con mini-player

#### 🎵 **ExoPlayerView**
- Componente de reproducción con lifecycle
- Soporte para múltiples formatos de stream
- Overlay de información de canal

## 🚀 Instalación y Uso

### **Requisitos**
- Android Studio Hedgehog+ (2023.1.1)
- Android TV/Fire TV device o emulador
- Java 17
- Gradle 8.13+

### **Compilación**
```bash
# Clonar el repositorio
git clone [URL_DEL_REPO]
cd IPTVMATE

# Compilar el proyecto
./gradlew build

# Instalar en dispositivo
./gradlew installDebug
```

### **Configuración**
1. **Fuentes de datos**: Las URLs de M3U y EPG están configuradas en `ChannelRepository.kt`
2. **Player backend**: ExoPlayer por defecto, preparado para migrar a LibVLC
3. **Tema**: Colores y estilos en `ui/theme/`

## 🎮 Controles de Navegación

### **Control Remoto (DPAD)**
- **⬅️ Izquierda**: Navegar hacia atrás en menús
- **➡️ Derecha**: Avanzar entre áreas (menu → categories → channels → epg)
- **⬆️ Arriba/⬇️ Abajo**: Navegar dentro de listas
- **🔘 Centro**: Seleccionar elemento

### **Estados de Foco**
1. **"menu"**: Sidebar principal
2. **"categories"**: Panel de categorías
3. **"channels"**: Lista de canales en EPG
4. **"epg"**: Grid de programas

### **Colapso de Menús**
- **Nivel 1**: Sidebar se colapsa a íconos
- **Nivel 2**: Categorías se ocultan
- **Nivel 3**: Solo queda visible el EPG grid

## 🔧 Personalización

### **Agregar Nuevas Fuentes**
```kotlin
// En ChannelRepository.kt
class ChannelRepository {
    private val m3uUrl = "TU_URL_M3U"
    private val epgUrl = "TU_URL_XMLTV"
}
```

### **Cambiar Player Backend**
```kotlin
// Implementar PlayerManager para LibVLC
class VLCPlayerManager : PlayerManager {
    // Implementación específica de VLC
}
```

### **Personalizar Tema**
```kotlin
// En ui/theme/Color.kt
val PrimaryColor = Color(0xFF4FC3F7)
val BackgroundColor = Color(0xFF0F1419)
```

## 🐛 Solución de Problemas

### **Problemas Comunes**

1. **"Sin señal" en preview**
   - Verificar conectividad de red
   - Comprobar URLs de streams en M3U
   - Revisar permisos de red en AndroidManifest.xml

2. **Navegación DPAD no funciona**
   - Verificar que el dispositivo esté en modo TV
   - Comprobar focus requests en composables

3. **EPG no carga**
   - Verificar formato XMLTV
   - Comprobar parsing en EPGRepository

### **Logs de Debug**
```bash
# Ver logs de la aplicación
adb logcat | grep IPTVMATE
```

## 🚀 Roadmap

### **Próximas Características**
- [ ] Integración LibVLC completa
- [ ] Sistema de favoritos
- [ ] Control parental
- [ ] Soporte DRM
- [ ] Grabación de programas
- [ ] Múltiples perfiles de usuario
- [ ] Sincronización en la nube

### **Mejoras Técnicas**
- [ ] Tests unitarios completos
- [ ] CI/CD pipeline
- [ ] Optimización de memoria
- [ ] Soporte offline

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Soporte

Para reportar bugs o solicitar features, por favor abre un issue en GitHub.

---

**Desarrollado con ❤️ para la comunidad IPTV**