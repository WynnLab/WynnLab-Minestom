package com.wynnlab.minestom.discord

import com.wynnlab.minestom.*
import com.wynnlab.minestom.util.post
import discord.events.MessageCreateEvent
import discord.events.ReadyEvent
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.kyori.adventure.text.Component
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

val discordChatWebhookUrl = getProperty("discord-chat-webhook-url")
val discordLogWebhookUrl = getProperty("discord-log-webhook-url")
val discordBotToken = getProperty("discord-bot-token")
val discordChannelId = getProperty("discord-channel-id")?.toLong()

inline fun postChatWebhook(json: JsonObjectBuilder.() -> Unit) {
    if (discordChatWebhookUrl != null) post(discordChatWebhookUrl, buildJsonObject(json))
}

inline fun postLogWebhook(json: JsonObjectBuilder.() -> Unit) {
    if (discordLogWebhookUrl != null) post(discordLogWebhookUrl, buildJsonObject(json))
}

inline fun postWebhooks(json: JsonObjectBuilder.() -> Unit) {
    postChatWebhook(json)
    postLogWebhook(json)
}

@OptIn(ExperimentalTime::class)
fun initDiscordClient() {
    if (discordBotToken == null || discordChannelId == null) return

    val client = discord.Client(guildReadyTimeout = Duration.seconds(1))
    client.event<ReadyEvent> {
        println("Discord bot initialized.")
    }
    client.event<MessageCreateEvent> { event ->
        if (event.message.channelId != discordChannelId) return@event
        if (event.message.content.isEmpty()) return@event
        if (event.message.author.bot) return@event
        broadcast(discordBroadcastPrefix.append(Component.text(event.message.content)))
        postLogWebhook {
            put("username", event.message.author.toString())
            event.message.author.avatar?.let { avatar ->
                put("avatar_url", "https://cdn.discordapp.com/avatars/${event.message.author.id}/$avatar.png")
            }
            put("content", event.message.content)
        }
    }
    client.run(discordBotToken)
}

private val discordBroadcastPrefix = Component.text()
    .append(Component.text("[", COLOR_DARKER_GRAY.textColor))
    .append(Component.text("Discord", COLOR_DISCORD.textColor))
    .append(Component.text("] ", COLOR_DARKER_GRAY.textColor))
    .build()