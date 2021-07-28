package com.wynnlab.minestom.listeners

import com.wynnlab.minestom.COLOR_GRAY
import com.wynnlab.minestom.COLOR_GREEN
import com.wynnlab.minestom.COLOR_RED
import com.wynnlab.minestom.textColor
import com.wynnlab.minestom.util.listen
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

val serverListenersNode = EventNode.all("server-listeners")

val serverPlayerListenersNode = EventNode.type("server-player-listeners", EventFilter.PLAYER)

fun onPlayerSpawn(e: PlayerSpawnEvent) {
    if (!e.isFirstSpawn) return
    val player = e.player
    Audiences.server().sendMessage(Component.text()
        .append(Component.text("[", COLOR_GRAY.textColor))
        .append(Component.text("+", COLOR_GREEN.textColor))
        .append(Component.text("] ", COLOR_GRAY.textColor))
        .append(player.name)
        .build())
}

fun onPlayerDisconnect(e: PlayerDisconnectEvent) {
    val player = e.player
    Audiences.server().sendMessage(Component.text()
        .append(Component.text("[", COLOR_GRAY.textColor))
        .append(Component.text("-", COLOR_RED.textColor))
        .append(Component.text("] ", COLOR_GRAY.textColor))
        .append(player.name)
        .build())
}

fun onPlayerChat(e: PlayerChatEvent) {
    e.setChatFormat {
        Component.text()
            .append(it.player.name)
            .append(Component.text(": "))
            .append(LegacyComponentSerializer.legacy('§').deserialize(it.message))
            .build()
    }
}

fun onPlayerDeath(e: PlayerDeathEvent) {
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