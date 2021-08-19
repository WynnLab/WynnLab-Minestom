package com.wynnlab.minestom.listeners

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wynnlab.minestom.*
import com.wynnlab.minestom.commands.Command
import com.wynnlab.minestom.commands.consoleIgnoreCommands
import com.wynnlab.minestom.util.listen
import com.wynnlab.minestom.util.post
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.*
import net.minestom.server.tag.Tag
import java.util.*

val serverListenersNode = EventNode.all("server-listeners")

val serverPlayerListenersNode = EventNode.type("server-player-listeners", EventFilter.PLAYER)

private fun onPlayerSpawn(e: PlayerSpawnEvent) {
    if (!e.isFirstSpawn) return
    println(e.player.instance)

    val player = e.player
    broadcast(Component.text()
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

    player.permissionLevel = permissionLevel(player.uuid)
}

private fun onPlayerDisconnect(e: PlayerDisconnectEvent) {
    val player = e.player
    broadcast(Component.text()
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
        val format = Component.text()
            .append(Component.text("[106/Cl", NamedTextColor.GRAY))
        if (it.player.hasTag(Tag.String("guild_tag")))
            format.append(Component.text("/${it.player.getTag(Tag.String("guild_tag"))}", NamedTextColor.GRAY))
        format
            .append(Component.text("] ", NamedTextColor.GRAY))
            .append(it.player.name)
            .append(Component.text(": ", (it.player.name as TextComponent).color()))
            .append(LegacyComponentSerializer.legacy('§').deserialize(it.message))
            .build()
        format.build()
    }

    if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            addProperty("content", "**${e.player.username}:** ${e.message}")
        })
}

private fun onPlayerDeath(e: PlayerDeathEvent) {
    val deathMessage = Component.text().append(e.player.name)
        .append(Component.text(" thought this would be as easy as Wynncraft. Wrong.")).build()
    e.chatMessage = deathMessage
    e.player.isEnableRespawnScreen = false
}

private fun onPlayerCommand(e: PlayerCommandEvent) {
    if (consoleIgnoreCommands.contains(e.command)) return
    Audiences.console().sendMessage(Component.text("${e.player.username} issued command: ${e.command}"))
}

fun initServerListeners(globalEventHandler: GlobalEventHandler) {
    serverPlayerListenersNode.listen(::onPlayerSpawn)
    serverListenersNode.listen(::onPlayerDisconnect)
    serverListenersNode.listen(::onPlayerChat)
    serverListenersNode.listen(::onPlayerDeath)
    serverListenersNode.listen(::onPlayerCommand)

    serverListenersNode.addChild(serverPlayerListenersNode)
    globalEventHandler.addChild(serverListenersNode)
}

private fun permissionLevel(uuid: UUID) = if (uuid.toString() == "4182ab6a-4698-41ec-be41-62fb4451b26a") 4 else 0