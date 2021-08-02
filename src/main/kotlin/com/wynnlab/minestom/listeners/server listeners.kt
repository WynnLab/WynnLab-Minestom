package com.wynnlab.minestom.listeners

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wynnlab.minestom.*
import com.wynnlab.minestom.util.listen
import com.wynnlab.minestom.util.post
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

val serverListenersNode = EventNode.all("server-listeners")

val serverPlayerListenersNode = EventNode.type("server-player-listeners", EventFilter.PLAYER)

private fun onPlayerSpawn(e: PlayerSpawnEvent) {
    if (!e.isFirstSpawn) return

    val player = e.player
    Audiences.server().sendMessage(Component.text()
        .append(Component.text("[", COLOR_GRAY.textColor))
        .append(Component.text("+", COLOR_GREEN.textColor))
        .append(Component.text("] ", COLOR_GRAY.textColor))
        .append(player.name)
        .build())

    if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            add("embeds", JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("color", 0x00ff00)
                    add("author", JsonObject().apply {
                        addProperty("name", "${player.username} has joined the server")
                        addProperty("icon_url", "https://www.mc-heads.net/avatar/${player.uuid}")
                    })
                })
            })
        })

    // Debug
    player.gameMode = GameMode.CREATIVE
    player.permissionLevel = 3
}

private fun onPlayerDisconnect(e: PlayerDisconnectEvent) {
    val player = e.player
    Audiences.server().sendMessage(Component.text()
        .append(Component.text("[", COLOR_GRAY.textColor))
        .append(Component.text("-", COLOR_RED.textColor))
        .append(Component.text("] ", COLOR_GRAY.textColor))
        .append(player.name)
        .build())

    if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            add("embeds", JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("color", 0xff0000)
                    add("author", JsonObject().apply {
                        addProperty("name", "${player.username} has left the server")
                        addProperty("icon_url", "https://www.mc-heads.net/avatar/${player.uuid}")
                    })
                })
            })
        })
}

private fun onPlayerChat(e: PlayerChatEvent) {
    e.setChatFormat {
        Component.text()
            .append(it.player.name)
            .append(Component.text(": "))
            .append(LegacyComponentSerializer.legacy('§').deserialize(it.message))
            .build()
    }

    if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            addProperty("content", "**<${e.player.username}>** ${e.message}")
        })
}

private fun onPlayerDeath(e: PlayerDeathEvent) {
    val deathMessage = Component.text().append(e.player.name)
        .append(Component.text(" thought this would be as easy as Wynncraft. Wrong.")).build()
    e.chatMessage = deathMessage
    e.player.isEnableRespawnScreen = false
}

fun initServerListeners(globalEventHandler: GlobalEventHandler) {
    serverPlayerListenersNode.listen(::onPlayerSpawn)
    serverListenersNode.listen(::onPlayerDisconnect)
    serverListenersNode.listen(::onPlayerChat)
    serverListenersNode.listen(::onPlayerDeath)

    serverListenersNode.addChild(serverPlayerListenersNode)
    globalEventHandler.addChild(serverListenersNode)
}