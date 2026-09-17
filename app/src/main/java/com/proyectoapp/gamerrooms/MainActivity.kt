package com.proyectoapp.gamerrooms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AssistChip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamerRoomsTheme {
                GamerRoomsApp()
            }
        }
    }
}

private data class GamerGroup(
    val name: String,
    val game: String,
    val platform: String,
    val region: String,
    val members: Int,
    val status: String,
    val tags: List<String>,
)

private data class ChatMessage(
    val author: String,
    val text: String,
    val time: String,
)

private data class UserProfile(
    val fullName: String,
    val gamerTag: String,
    val email: String,
    val region: String,
    val platform: String,
    val favoriteGames: List<String>,
)

private enum class AuthMode {
    Login,
    Register
}

private enum class MainSection {
    Groups,
    Friends,
    Profile
}

private val sampleGroups = listOf(
    GamerGroup("Ranked Night Ops", "Valorant", "PC", "LATAM", 128, "Buscando duo", listOf("Competitivo", "18+", "Mic")),
    GamerGroup("Guilda del Nexus", "League of Legends", "PC", "LAN", 342, "Scrims hoy", listOf("Flex", "Clash", "Coach")),
    GamerGroup("Drop Zone Squad", "Fortnite", "Crossplay", "NA/LATAM", 89, "Cupos abiertos", listOf("Casual", "Zero Build")),
    GamerGroup("Raid Cero", "Destiny 2", "PlayStation", "LATAM", 56, "Raid a las 21:00", listOf("PvE", "Endgame")),
)

private val sampleMessages = listOf(
    ChatMessage("Nyx", "Tenemos cupo para controller. Quien se suma?", "20:14"),
    ChatMessage("PixelMage", "Yo entro despues de una partida corta.", "20:16"),
    ChatMessage("Raptor", "Perfecto, dejen el codigo de sala fijado.", "20:18"),
)

private val defaultProfile = UserProfile(
    fullName = "Invitado Gamer",
    gamerTag = "PlayerOne",
    email = "player@gamerrooms.app",
    region = "LATAM",
    platform = "PC",
    favoriteGames = listOf("Valorant", "Fortnite")
)

private val discoverableFriends = listOf(
    UserProfile("Camila Torres", "NyxCarry", "nyx@gamerrooms.app", "LATAM", "PC", listOf("Valorant", "League of Legends")),
    UserProfile("Mateo Rios", "PixelMage", "pixel@gamerrooms.app", "LAN", "PlayStation", listOf("Destiny 2", "Fortnite")),
    UserProfile("Sara Vega", "RaidQueen", "sara@gamerrooms.app", "NA/LATAM", "Xbox", listOf("Call of Duty", "Minecraft")),
    UserProfile("Luis Moreno", "ZeroBuild", "zero@gamerrooms.app", "LATAM", "Nintendo", listOf("Fortnite", "Minecraft")),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamerRoomsApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var myProfile by remember { mutableStateOf(defaultProfile) }

    if (!isLoggedIn) {
        LoginScreen(
            onLogin = { isLoggedIn = true },
            onRegister = { profile ->
                myProfile = profile
                isLoggedIn = true
            }
        )
        return
    }

    var selectedSection by remember { mutableStateOf(MainSection.Groups) }
    var query by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(sampleGroups.first()) }
    val friends = remember { mutableStateListOf<UserProfile>() }
    val filteredGroups = sampleGroups.filter {
        it.name.contains(query, ignoreCase = true) ||
            it.game.contains(query, ignoreCase = true) ||
            it.platform.contains(query, ignoreCase = true)
    }

    val messagesPerGroup = remember {
        mutableStateMapOf<String, SnapshotStateList<ChatMessage>>().apply {
            sampleGroups.forEach { group ->
                put(group.name, sampleMessages.toMutableStateList())
            }
        }
    }

    val currentMessages = messagesPerGroup[selectedGroup.name] ?: remember(selectedGroup.name) {
        mutableStateListOf<ChatMessage>().also { messagesPerGroup[selectedGroup.name] = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("GamerRooms", fontWeight = FontWeight.Bold)
                }
            )
        },
        containerColor = Color(0xFF101216)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionTabs(
                    selectedSection = selectedSection,
                    onSectionSelected = { selectedSection = it }
                )
            }

            when (selectedSection) {
                MainSection.Groups -> {
                    item {
                        HeaderPanel()
                    }
                    item {
                        SearchBox(query = query, onQueryChange = { query = it })
                    }
                    itemsIndexed(filteredGroups) { index, group ->
                        AnimatedGroupCard(
                            index = index,
                            group = group,
                            isSelected = group == selectedGroup,
                            onJoin = { selectedGroup = group }
                        )
                        // Insertamos publicidad después del segundo elemento (índice 1)
                        if (index == 1 && filteredGroups.size > 1) {
                            Spacer(Modifier.height(16.dp))
                            AdCard()
                        }
                    }
                    item {
                        ChatRoom(
                            group = selectedGroup,
                            messages = currentMessages,
                            onSendMessage = { text ->
                                currentMessages.add(ChatMessage("Tú", text, "Ahora"))
                            }
                        )
                    }
                }
                MainSection.Friends -> {
                    item {
                        FriendsSection(
                            friends = friends,
                            onAddFriend = { friend ->
                                if (friends.none { it.gamerTag == friend.gamerTag }) {
                                    friends.add(friend)
                                }
                            }
                        )
                    }
                }
                MainSection.Profile -> {
                    item {
                        ProfileSection(profile = myProfile, onLogout = { isLoggedIn = false })
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit, onRegister: (UserProfile) -> Unit) {
    var authMode by remember { mutableStateOf(AuthMode.Login) }

    when (authMode) {
        AuthMode.Login -> LoginForm(
            onLogin = onLogin,
            onCreateAccount = { authMode = AuthMode.Register }
        )
        AuthMode.Register -> RegisterForm(
            onRegister = onRegister,
            onBackToLogin = { authMode = AuthMode.Login }
        )
    }
}

@Composable
private fun LoginForm(onLogin: () -> Unit, onCreateAccount: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val canSubmit = email.isNotBlank() && password.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101216))
            .imePadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "GamerRooms",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF7CFFB2)
                )
                Text(
                    text = "Entra a tu cuenta y encuentra tu proxima sala gamer.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFD6E2DA)
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181B21)),
                border = BorderStroke(1.dp, Color(0xFF7CFFB2).copy(alpha = 0.18f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Iniciar sesion",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            showError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Correo o gamertag") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            showError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        label = { Text("Contrasena") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    if (showError) {
                        Text(
                            text = "Completa tus datos para continuar.",
                            color = Color(0xFFFFB4AB),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = {
                            if (canSubmit) {
                                onLogin()
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Entrar")
                    }
                    Text(
                        text = "Registro con Cognito pendiente para la siguiente iteracion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFAEB8B1)
                    )
                    TextButton(
                        onClick = onCreateAccount,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear cuenta nueva")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(onRegister: (UserProfile) -> Unit, onBackToLogin: () -> Unit) {
    val platformOptions = listOf("PC", "PlayStation", "Xbox", "Nintendo", "Mobile")

    // Juegos predefinidos con iconos por defecto de Android/Launcher ya que no hay recursos de imágenes reales en el proyecto.
    // Usaremos el ic_launcher_background / ic_launcher_foreground incorporados.
    val gameOptions = listOf(
        Pair("Valorant", R.drawable.valo),
        Pair("League of Legends", android.R.drawable.ic_menu_slideshow),
        Pair("Fortnite", android.R.drawable.ic_menu_compass),
        Pair("Destiny 2", android.R.drawable.ic_menu_agenda),
        Pair("Minecraft", android.R.drawable.ic_menu_mapmode),
        Pair("Call of Duty", android.R.drawable.ic_menu_myplaces)
    )

    // Control de paso (paso 1: Datos, paso 2: Plataforma, paso 3: Juegos)
    var currentStep by remember { mutableStateOf(1) }

    var fullName by remember { mutableStateOf("") }
    var gamerTag by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf(platformOptions.first()) }
    
    var selectedGames by remember { mutableStateOf(setOf<String>()) }
    var customGameInput by remember { mutableStateOf("") }
    var isDialogOpen by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val totalSelectedGames = remember(selectedGames, customGameInput) {
        val list = selectedGames.toMutableList()
        if (customGameInput.isNotBlank()) {
            list.add(customGameInput.trim())
        }
        list
    }

    val step1Valid = fullName.isNotBlank() && gamerTag.isNotBlank() && email.isNotBlank() && password.isNotBlank() && region.isNotBlank()
    val step2Valid = selectedPlatform.isNotBlank()
    val canSubmit = step1Valid && step2Valid && totalSelectedGames.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101216))
            .imePadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = when(currentStep) {
                        1 -> "Paso 1: Tus Datos"
                        2 -> "Paso 2: Elige tu plataforma"
                        else -> "Paso 3: Juegos favoritos"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF7CFFB2)
                )
                Text(
                    text = when(currentStep) {
                        1 -> "Ingresa tu información personal básica para identificarte."
                        2 -> "Selecciona la consola o sistema principal donde juegas."
                        else -> "Elige los títulos que más te apasionan."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD6E2DA)
                )
                
                // Indicador de Progreso Visual Simple
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (index + 1 <= currentStep) Color(0xFF7CFFB2) else Color(0xFF242933))
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181B21)),
                border = BorderStroke(1.dp, Color(0xFF7CFFB2).copy(alpha = 0.18f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (currentStep == 1) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text("Nombre") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = gamerTag,
                            onValueChange = {
                                gamerTag = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Gamertag") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Correo") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            label = { Text("Contrasena") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        OutlinedTextField(
                            value = region,
                            onValueChange = {
                                region = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Region") },
                            singleLine = true
                        )
                        
                        if (showError) {
                            Text(
                                text = "Por favor, completa todos los campos para continuar.",
                                color = Color(0xFFFFB4AB),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                if (step1Valid) {
                                    currentStep = 2
                                    showError = false
                                } else {
                                    showError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Siguiente")
                        }
                    }

                    if (currentStep == 2) {
                        Text("Selecciona una plataforma", fontWeight = FontWeight.SemiBold, color = Color.White)
                        
                        val platformItems = listOf(
                            Triple("PC", Icons.Default.Computer, "Juegos de computadora"),
                            Triple("PlayStation", Icons.Default.SportsEsports, "Consola de Sony"),
                            Triple("Xbox", Icons.Default.VideogameAsset, "Consola de Microsoft"),
                            Triple("Nintendo", Icons.Default.SportsEsports, "Nintendo Switch"),
                            Triple("Mobile", Icons.Default.Smartphone, "Celulares y Tablets")
                        )

                        platformItems.forEach { (platform, icon, desc) ->
                            val isSelected = selectedPlatform == platform
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF1E3528) else Color(0xFF242933)
                                ),
                                border = BorderStroke(
                                    width = 1.5.dp,
                                    color = if (isSelected) Color(0xFF7CFFB2) else Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPlatform = platform }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = platform,
                                        tint = if (isSelected) Color(0xFF7CFFB2) else Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(platform, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF7CFFB2)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { currentStep = 1 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Atrás", color = Color.Gray)
                            }
                            Button(
                                onClick = { currentStep = 3 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Siguiente")
                            }
                        }
                    }

                    if (currentStep == 3) {
                        Text("Elige tus juegos favoritos", fontWeight = FontWeight.SemiBold, color = Color.White)
                        
                        // Botón dinámico para abrir la ventana / Dialog
                        Button(
                            onClick = { isDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242933)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF7CFFB2).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (totalSelectedGames.isEmpty()) "Seleccionar Juegos (${totalSelectedGames.size})" else "Juegos seleccionados (${totalSelectedGames.size})",
                                color = Color(0xFF7CFFB2)
                            )
                        }

                        // Chips informativos para mostrar lo seleccionado actualmente
                        if (totalSelectedGames.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                totalSelectedGames.take(4).forEach { game ->
                                    AssistChip(onClick = {}, label = { Text(game) })
                                }
                                if (totalSelectedGames.size > 4) {
                                    AssistChip(onClick = {}, label = { Text("+${totalSelectedGames.size - 4}") })
                                }
                            }
                        }

                        if (showError) {
                            Text(
                                text = "Por favor, abre la ventana y escoge al menos un juego.",
                                color = Color(0xFFFFB4AB),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { currentStep = 2 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Atrás", color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    if (canSubmit) {
                                        onRegister(
                                            UserProfile(
                                                fullName = fullName,
                                                gamerTag = gamerTag,
                                                email = email,
                                                region = region,
                                                platform = selectedPlatform,
                                                favoriteGames = totalSelectedGames
                                            )
                                )
                                    } else {
                                        showError = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Registrarme")
                            }
                        }
                    }

                    TextButton(
                        onClick = onBackToLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ya tengo cuenta")
                    }
                }
            }
        }
    }

    // Ventana modal (AlertDialog) dinámico para la selección de juegos
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = {
                Text(
                    "Selecciona tus juegos",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Elige de nuestra lista o ingresa un juego personalizado al final.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    // Cuadrícula de juegos con imágenes simbólicas y nombres
                    Box(modifier = Modifier.height(260.dp)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(gameOptions) { (gameName, imageRes) ->
                                val isSelected = gameName in selectedGames
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1E3528) else Color(0xFF242933)
                                    ),
                                    border = BorderStroke(
                                        width = 1.5.dp,
                                        color = if (isSelected) Color(0xFF7CFFB2) else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedGames = if (isSelected) {
                                                selectedGames - gameName
                                            } else {
                                                selectedGames + gameName
                                            }
                                            showError = false
                                        }
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            // Cambiado de Icon a Image para que muestre la foto real cargada a color en lugar de un icono plano monocromático.
                                            Image(
                                                painter = painterResource(id = imageRes),
                                                contentDescription = gameName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = gameName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = Color(0xFF7CFFB2),
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .size(18.dp)
                                                    .align(Alignment.TopEnd)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Opción abierta de "Otros"
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Otro juego (Opción abierta)", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = customGameInput,
                            onValueChange = { 
                                customGameInput = it
                                showError = false
                            },
                            placeholder = { Text("Ej. Minecraft, Elden Ring...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CFFB2), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Listo", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF181B21),
            textContentColor = Color.White,
            titleContentColor = Color.White
        )
    }
}

@Composable
private fun SectionTabs(selectedSection: MainSection, onSectionSelected: (MainSection) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selectedSection == MainSection.Groups,
            onClick = { onSectionSelected(MainSection.Groups) },
            label = { Text("Grupos") }
        )
        FilterChip(
            selected = selectedSection == MainSection.Friends,
            onClick = { onSectionSelected(MainSection.Friends) },
            label = { Text("Amigos") }
        )
        FilterChip(
            selected = selectedSection == MainSection.Profile,
            onClick = { onSectionSelected(MainSection.Profile) },
            label = { Text("Mi perfil") }
        )
    }
}

@Composable
private fun FriendsSection(friends: List<UserProfile>, onAddFriend: (UserProfile) -> Unit) {
    var friendQuery by remember { mutableStateOf("") }
    val results = discoverableFriends.filter { friend ->
        friend.fullName.contains(friendQuery, ignoreCase = true) ||
            friend.gamerTag.contains(friendQuery, ignoreCase = true) ||
            friend.favoriteGames.any { it.contains(friendQuery, ignoreCase = true) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Amigos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = friendQuery,
            onValueChange = { friendQuery = it },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Buscar por gamertag o juego") },
            singleLine = true
        )
        Text("Resultados", fontWeight = FontWeight.SemiBold)
        results.forEach { friend ->
            FriendCard(
                profile = friend,
                isFriend = friends.any { it.gamerTag == friend.gamerTag },
                onAddFriend = { onAddFriend(friend) }
            )
        }
        Text("Mis amigos", fontWeight = FontWeight.SemiBold)
        if (friends.isEmpty()) {
            Text("Aun no has agregado amigos.", color = Color(0xFFAEB8B1))
        } else {
            friends.forEach { friend ->
                FriendCard(profile = friend, isFriend = true, onAddFriend = {})
            }
        }
    }
}

@Composable
private fun FriendCard(profile: UserProfile, isFriend: Boolean, onAddFriend: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181B21)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF25322D)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF7CFFB2))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(profile.gamerTag, fontWeight = FontWeight.Bold)
                    Text("${profile.fullName} - ${profile.platform} - ${profile.region}", color = Color(0xFFAEB8B1))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.favoriteGames.take(3).forEach { game ->
                    AssistChip(onClick = {}, label = { Text(game) })
                }
            }
            Button(
                onClick = onAddFriend,
                enabled = !isFriend,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isFriend) "Agregado" else "Agregar amigo")
            }
        }
    }
}

@Composable
private fun ProfileSection(profile: UserProfile, onLogout: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 320)) +
                slideInVertically(
                    animationSpec = tween(durationMillis = 320),
                    initialOffsetY = { it / 4 }
                )
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181B21)),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(profile.gamerTag, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text(profile.fullName, color = Color(0xFFD6E2DA))
                    ProfileRow("Correo", profile.email)
                    ProfileRow("Region", profile.region)
                    ProfileRow("Plataforma", profile.platform)
                    Text("Juegos de interes", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        profile.favoriteGames.take(3).forEachIndexed { index, game ->
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = index * 90)) +
                                    slideInVertically(
                                        animationSpec = tween(durationMillis = 220, delayMillis = index * 90),
                                        initialOffsetY = { it / 2 }
                                    )
                            ) {
                                AssistChip(onClick = {}, label = { Text(game) })
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935), contentColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFFAEB8B1))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HeaderPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1B4D3E), Color(0xFF26324A))
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Encuentra tu squad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Busca comunidades por juego, plataforma o region y entra directo a una sala activa.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD6E2DA)
            )
        }
    }
}

@Composable
private fun SearchBox(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        label = { Text("Buscar grupos") },
        singleLine = true
    )
}

@Composable
private fun AdCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E23)),
        border = BorderStroke(1.dp, Color(0xFF7CFFB2).copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFF7CFFB2),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "SPONSORED",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF102016),
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Gamer Room Pro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Obtén salas privadas y emojis exclusivos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAEB8B1)
                )
            }
            Button(
                onClick = { /* Acción de la publicidad */ },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Saber más")
            }
        }
    }
}

@Composable
private fun AnimatedGroupCard(index: Int, group: GamerGroup, isSelected: Boolean, onJoin: () -> Unit) {
    var visible by remember(group.name) { mutableStateOf(false) }

    LaunchedEffect(group.name) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 280, delayMillis = index * 70)) +
            slideInVertically(
                animationSpec = tween(durationMillis = 280, delayMillis = index * 70),
                initialOffsetY = { it / 3 }
            )
    ) {
        GroupCard(
            group = group,
            isSelected = isSelected,
            onJoin = onJoin
        )
    }
}

@Composable
private fun GroupCard(group: GamerGroup, isSelected: Boolean, onJoin: () -> Unit) {
    val cardColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF1D2A24) else Color(0xFF181B21),
        animationSpec = tween(durationMillis = 260),
        label = "groupCardColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF7CFFB2).copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 260),
        label = "groupBorderColor"
    )
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 50.dp else 44.dp,
        animationSpec = tween(durationMillis = 260),
        label = "groupIconSize"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(iconSize),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF25322D)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF7CFFB2))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(group.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${group.game} - ${group.platform} - ${group.region}", color = Color(0xFFAEB8B1))
                }
            }
            Text(group.status, color = Color(0xFF7CFFB2), fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                group.tags.take(3).forEach { tag ->
                    AssistChip(onClick = {}, label = { Text(tag) })
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${group.members} miembros", color = Color(0xFFAEB8B1), modifier = Modifier.weight(1f))
                Button(onClick = onJoin, shape = RoundedCornerShape(8.dp)) {
                    Text(if (isSelected) "En sala" else "Unirme")
                }
            }
        }
    }
}

@Composable
private fun ChatRoom(
    group: GamerGroup,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit
) {
    var draft by remember(group.name) { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181B21)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Sala: ${group.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            messages.forEach { message ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF20242C), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row {
                        Text(message.author, fontWeight = FontWeight.Bold, color = Color(0xFF7CFFB2))
                        Spacer(Modifier.weight(1f))
                        Text(message.time, color = Color(0xFF8D9891))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(message.text)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Mensaje") },
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onSendMessage(draft)
                            draft = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                }
            }
        }
    }
}

@Composable
private fun GamerRoomsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7CFFB2),
            secondary = Color(0xFF8DB7FF),
            background = Color(0xFF101216),
            surface = Color(0xFF181B21),
            onPrimary = Color(0xFF102016),
            onSecondary = Color(0xFF0E1724),
            onBackground = Color(0xFFE7ECE8),
            onSurface = Color(0xFFE7ECE8)
        ),
        content = content
    )
}

