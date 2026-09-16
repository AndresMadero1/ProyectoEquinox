package com.proyectoapp.gamerrooms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamerRoomsApp() {
    var query by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(sampleGroups.first()) }
    val filteredGroups = sampleGroups.filter {
        it.name.contains(query, ignoreCase = true) ||
            it.game.contains(query, ignoreCase = true) ||
            it.platform.contains(query, ignoreCase = true)
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
                HeaderPanel()
            }
            item {
                SearchBox(query = query, onQueryChange = { query = it })
            }
            items(filteredGroups) { group ->
                GroupCard(
                    group = group,
                    isSelected = group == selectedGroup,
                    onJoin = { selectedGroup = group }
                )
            }
            item {
                ChatRoom(group = selectedGroup)
            }
        }
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
private fun GroupCard(group: GamerGroup, isSelected: Boolean, onJoin: () -> Unit) {
    val cardColor = if (isSelected) Color(0xFF1D2A24) else Color(0xFF181B21)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
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
private fun ChatRoom(group: GamerGroup) {
    var draft by remember(group.name) { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181B21)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Sala: ${group.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            sampleMessages.forEach { message ->
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
                IconButton(onClick = { draft = "" }) {
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

