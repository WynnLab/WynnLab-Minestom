@file:JvmName("Main")

package com.wynnlab.minestom

import com.google.gson.JsonParser
import com.wynnlab.minestom.commands.*
import com.wynnlab.minestom.generator.GeneratorDemo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.lan.OpenToLAN
import net.minestom.server.extras.lan.OpenToLANConfig
import net.minestom.server.ping.ServerListPingType
import net.minestom.server.utils.Position
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun main() {
    val server = MinecraftServer.init()

    MinecraftServer.setBrandName("WynnLab")

    val connectionManager = MinecraftServer.getConnectionManager()
    connectionManager.setUuidProvider { _, username ->
        val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "GET"
        http.connect()
        if (http.responseCode != 200) {
            MinecraftServer.LOGGER.warn("Could not get UUID for player $username")
            return@setUuidProvider UUID.randomUUID()
        }
        val response = JsonParser.parseReader(url.openStream().reader()).asJsonObject
        val id = response.get("id").asString
        val uuid = UUID(id.substring(0, 16).toULong(16).toLong(), id.substring(16).toULong(16).toLong())
        MinecraftServer.LOGGER.info("Player $username logged in with UUID $uuid")
        uuid
    }

    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    instanceContainer.chunkGenerator = GeneratorDemo()

    val commandManager = MinecraftServer.getCommandManager()
    commandManager.register(StopCommand)
    commandManager.register(PermissionCommand)

    commandManager.register(ClassCommand)
    commandManager.register(CastCommand)
    commandManager.register(ItemCommand)
    commandManager.register(DummyCommand)

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Position(0.0, 42.0, 0.0)
        player.gameMode = GameMode.CREATIVE
        player.permissionLevel = 4
    }

    globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
        val responseData = event.responseData
        responseData.version = "WynnLab (1.17)"
        responseData.description = when (event.pingType) {
            ServerListPingType.MODERN_FULL_RGB -> motd
            ServerListPingType.OPEN_TO_LAN -> lanMotd
            else -> legacyMotd
        }//if (event.pingType == ServerListPingType.MODERN_FULL_RGB) motd else legacyMotd
    }

    server.start("0.0.0.0", 25565)

    OpenToLAN.open(OpenToLANConfig())
}

private val motd = Component.text()
    .append(Component.text("                     play.", COLOR_DARKER_GRAY.textColor))
    .append(Component.text("WYNNLAB", Style.style(COLOR_WYNN.textColor, TextDecoration.BOLD)))
    .append(Component.text(".tk", COLOR_DARKER_GRAY.textColor))
    .append(Component.text("                beta ", NamedTextColor.GRAY))
    .append(Component.text("✦", COLOR_ORANGE.textColor))
    .append(Component.newline())
    .append(Component.text("              Wynn", Style.style(COLOR_PINK.textColor, TextDecoration.BOLD)))
    .append(Component.text(" brought to ", COLOR_PURPLE.textColor))
    .append(Component.text("1.17", Style.style(COLOR_PINK.textColor, TextDecoration.BOLD)))
    .build()

private val lanMotd = LegacyComponentSerializer.legacy('§').deserialize("                     §8play.§b§lWYNNLAB§8.tk")
private val legacyMotd = Component.text().append(lanMotd).append(Component.newline()).append(
    LegacyComponentSerializer.legacy('§').deserialize("              §d§lWynn §5brought to §d§l1.17")).build()