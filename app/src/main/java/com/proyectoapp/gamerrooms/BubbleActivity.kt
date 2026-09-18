package com.proyectoapp.gamerrooms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BubbleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val friendId = intent.getIntExtra("friend_id", 0)
        val friendName = intent.getStringExtra("friend_name") ?: "Chat"
        val myId = intent.getIntExtra("my_id", 0)
        val myTag = intent.getStringExtra("my_tag") ?: "Yo"

        setContent {
            GamerRoomsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF101216)) {
                    BubbleChatScreen(friendId, friendName, myId, myTag)
                }
            }
        }
    }
}

@Composable
fun BubbleChatScreen(friendId: Int, friendName: String, myId: Int, myTag: String) {
    val scope = rememberCoroutineScope()
    val directMessages = remember { mutableStateListOf<ChatMessage>() }
    val dmId = if (myId < friendId) "DM:${myId}_$friendId" else "DM:${friendId}_$myId"
    
    LaunchedEffect(dmId) {
        while (true) {
            try {
                val dbMessages = SupabaseHelper.client.from("mensajes")
                    .select {
                        filter { eq("grupo", dmId) }
                    }.decodeList<ChatMessage>()
                directMessages.clear()
                directMessages.addAll(dbMessages)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(3000)
        }
    }

    ChatRoom(
        group = GamerGroup(friendName, "Chat Privado", "", "", 2, "En línea", emptyList()),
        messages = directMessages,
        onSendMessage = { text ->
            val dmMsg = ChatMessage(
                grupo = dmId, 
                author = myTag, 
                text = text, 
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
            scope.launch {
                try {
                    SupabaseHelper.client.from("mensajes").insert(dmMsg)
                    directMessages.add(dmMsg)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    )
}
