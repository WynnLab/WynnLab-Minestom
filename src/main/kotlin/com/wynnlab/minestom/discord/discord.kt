package com.wynnlab.minestom.discord

import com.google.gson.JsonObject
import com.wynnlab.minestom.*
import com.wynnlab.minestom.util.post
import discord.Client
import discord.DiscordApplication
import discord.Message
import discord.events.Event
import discord.events.MessageCreateEvent
import net.kyori.adventure.text.Component

val discordWebhookUrl = getProperty("discord-webhook-url")
val discordBotToken = getProperty("discord-bot-token")
val discordChannelId = getProperty("discord-channel-id")?.toLong()

inline fun postWebhook(json: () -> JsonObject) {
    if (discordWebhookUrl != null) post(discordWebhookUrl, json())
}

fun initDiscordClient() {
    if (discordBotToken == null || discordChannelId == null) return
    (object : DiscordApplication(Client()) {
        fun run() {
            client.run(discordBotToken)
        }

        //TODO
        private val channelIdField = Message::class.java.getDeclaredField("channelId").apply { isAccessible = true }

        @Event
        @Suppress("unused", "RedundantSuspendModifier")
        suspend fun onMessageCreate(event: MessageCreateEvent) {
            if (channelIdField.get(event.message) != discordChannelId) return
            broadcast(Component.text()
                .append(discordBroadcastPrefix)
                .append(Component.text("${event.message.author}: "))
                .append(Component.text(event.message.content))
                .build())
        }
    }).run()
}

private val discordBroadcastPrefix = Component.text()
    .append(Component.text("[", COLOR_DARKER_GRAY.textColor))
    .append(Component.text("Discord", COLOR_DISCORD.textColor))
    .append(Component.text("] ", COLOR_DARKER_GRAY.textColor))
    .build()