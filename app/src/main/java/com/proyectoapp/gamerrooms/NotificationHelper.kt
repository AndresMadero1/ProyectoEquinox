package com.proyectoapp.gamerrooms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

object NotificationHelper {
    private const val CHANNEL_ID = "gamer_rooms_chat"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GamerRooms Chats"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = "Notificaciones de chats y burbujas"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setAllowBubbles(true)
                }
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showChatBubble(context: Context, friend: UserProfile, lastMessage: String, myId: Int, myGamerTag: String) {
        val shortcutId = "chat_${friend.id}"
        
        // 1. Create Person
        val person = Person.Builder()
            .setName(friend.gamerTag)
            .setImportant(true)
            .build()

        // 2. Create Shortcut
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
        }
        
        val shortcut = ShortcutInfoCompat.Builder(context, shortcutId)
            .setLocusId(LocusIdCompat(shortcutId))
            .setShortLabel(friend.gamerTag)
            .setIntent(intent)
            .setLongLived(true)
            .setPerson(person)
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)

        // 3. Create Bubble Metadata
        val bubbleIntent = Intent(context, BubbleActivity::class.java).apply {
            putExtra("friend_id", friend.id)
            putExtra("friend_name", friend.gamerTag)
            putExtra("my_id", myId)
            putExtra("my_tag", myGamerTag)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, bubbleIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        
        val bubbleData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationCompat.BubbleMetadata.Builder(pendingIntent, IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                .setDesiredHeight(600)
                .setAutoExpandBubble(true)
                .build()
        } else null

        // 4. Build Notification
        val messagingStyle = NotificationCompat.MessagingStyle(person)
            .addMessage(lastMessage, System.currentTimeMillis(), person)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(friend.gamerTag)
            .setContentText(lastMessage)
            .setStyle(messagingStyle)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setShortcutId(shortcutId)
            .addPerson(person)
            .setBubbleMetadata(bubbleData)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(friend.id ?: 0, builder.build())
    }
}
