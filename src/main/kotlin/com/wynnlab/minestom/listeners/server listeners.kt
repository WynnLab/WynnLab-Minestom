package com.wynnlab.minestom.listeners

import com.wynnlab.minestom.*
import com.wynnlab.minestom.commands.component
import com.wynnlab.minestom.commands.consoleIgnoreCommands
import com.wynnlab.minestom.discord.postLogWebhook
import com.wynnlab.minestom.discord.postWebhooks
import com.wynnlab.minestom.util.listen
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.advancements.FrameType
import net.minestom.server.advancements.notifications.Notification
import net.minestom.server.advancements.notifications.NotificationCenter
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.*
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
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


    postWebhooks {
        val authorName = "${player.username} has joined the server"
        val authorIcon = "https://www.mc-heads.net/avatar/${player.uuid}"
        putJsonArray("embeds") {
            addJsonObject {
                put("color", 0x00ff00)
                putJsonObject("author") {
                    put("name", authorName)
                    put("icon_url", authorIcon)
                }
            }
        }
    }

    player.sendMessage(languageText)

    NotificationCenter.send(welcomeNotification, player)

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


    postWebhooks {
        val authorName = "${player.username} has left the server"
        val authorIcon = "https://www.mc-heads.net/avatar/${player.uuid}"
        putJsonArray("embeds") {
            addJsonObject {
                put("color", 0xff0000)
                putJsonObject("author") {
                    put("name", authorName)
                    put("icon_url", authorIcon)
                }
            }
        }
    }
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

    postWebhooks {
        val username = e.player.username
        val avatar = "https://www.mc-heads.net/avatar/${e.player.uuid}"
        val content = e.message
        put("username", username)
        put("avatar_url", avatar)
        put("content", content)
    }
}

const val NUM_DEATH_MESSAGES = 1

private fun onPlayerDeath(e: PlayerDeathEvent) {
    val deathMessageId = random.nextInt(NUM_DEATH_MESSAGES) + 1
    val deathMessage = Component.translatable("death_message_$deathMessageId", e.player.component)
    e.chatMessage = deathMessage
    e.player.isEnableRespawnScreen = false
}

private fun onPlayerCommand(e: PlayerCommandEvent) {
    if (consoleIgnoreCommands.contains(e.command)) return
    Audiences.console().sendMessage(Component.text("${e.player.username} issued command: ${e.command}"))
    postLogWebhook {
        val username = e.player.username
        val avatar = "https://www.mc-heads.net/avatar/${e.player.uuid}"
        val content = "**>** /${e.command}"
        put("username", username)
        put("avatar_url", avatar)
        put("content", content)
    }
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


private val welcomeNotification = Notification(Component.text()
    .append(Component.translatable("notification.welcome.welcome"))
    .append(Component.newline())
    .append(Component.translatable("notification.welcome.help"))
    .build(),
    FrameType.TASK, ItemStack.of(Material.COMPASS))

private val languageText = Component.text()
    .append(Component.translatable("narrator.button.language", NamedTextColor.GRAY))
    .append(Component.text(": ", NamedTextColor.GRAY))
    .append(Component.translatable("language.name", COLOR_AQUA.textColor))
    .append(Component.text(" (", NamedTextColor.GRAY))
    .append(Component.translatable("language.region", NamedTextColor.GRAY))
    .append(Component.text(")", NamedTextColor.GRAY))
    .build()