@file:JvmName("Main")

package com.wynnlab.minestom

import com.google.gson.JsonObject
import com.wynnlab.minestom.commands.*
import com.wynnlab.minestom.generator.GeneratorDemo
import com.wynnlab.minestom.listeners.initServerListeners
import com.wynnlab.minestom.listeners.initWynnLabListeners
import com.wynnlab.minestom.players.WynnLabLogin
import com.wynnlab.minestom.players.WynnLabUuidProvider
import com.wynnlab.minestom.util.listen
import com.wynnlab.minestom.util.loadImageBase64
import com.wynnlab.minestom.util.post
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.lan.OpenToLAN
import net.minestom.server.extras.lan.OpenToLANConfig
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.ping.ServerListPingType
import net.minestom.server.storage.systems.FileStorageSystem
import net.minestom.server.utils.Position
import java.time.temporal.ChronoUnit
import kotlin.system.exitProcess

fun main() {
    val server = MinecraftServer.init()

    MinecraftServer.setBrandName("WynnLab")

    val connectionManager = MinecraftServer.getConnectionManager()
    connectionManager.setUuidProvider(WynnLabUuidProvider)

    val storageManager = MinecraftServer.getStorageManager()
    storageManager.defineDefaultStorageSystem(::FileStorageSystem)
    val storageLocation = storageManager.getLocation("world")

    val schedulerManager = MinecraftServer.getSchedulerManager()
    schedulerManager.buildShutdownTask(::saveAll).makeTransient().schedule()
    schedulerManager.buildTask(::saveAll).delay(1, ChronoUnit.MINUTES).repeat(5, ChronoUnit.MINUTES).schedule()

    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer(storageLocation)
    instanceContainer.chunkGenerator = GeneratorDemo()

    val commandManager = MinecraftServer.getCommandManager()
    registerServerCommands(commandManager)

    commandManager.register(ClassCommand)
    commandManager.register(CastCommand)
    commandManager.register(ItemCommand)
    commandManager.register(DummyCommand)
    commandManager.register(MenuCommand)
    registerPvpCommands(commandManager)
    registerEssentialsCommands(commandManager)
    registerDebugCommands(commandManager)

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.listen<PlayerLoginEvent> { event ->
        event.player.respawnPoint = Position(0.0, 42.0, 0.0)
        event.player.permissionLevel = 4
    }
    globalEventHandler.addListener(PlayerLoginEvent::class.java, WynnLabLogin(instanceContainer))

    globalEventHandler.listen<ServerListPingEvent> { event ->
        val responseData = event.responseData
        responseData.version = "WynnLab (1.17)"
        responseData.description = when (event.pingType) {
            ServerListPingType.MODERN_FULL_RGB -> motd
            ServerListPingType.OPEN_TO_LAN -> lanMotd
            else -> legacyMotd
        }
        if (faviconBase64 != null)
            responseData.favicon = faviconBase64
    }


    initServerListeners(globalEventHandler)
    initWynnLabListeners(globalEventHandler)

    val ip = getProperty("server-ip", "0.0.0.0")
    val port = getProperty("server-port", "25565").toIntOrNull() ?: 25565
    server.start(ip, port)

    if (getProperty("open-to-lan") == "true")
        OpenToLAN.open(OpenToLANConfig())

    if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            addProperty("content", ":green_circle: **Server started!**")
        })
}

fun saveAll() {
    broadcast(Component.text("[Server] saving...", NamedTextColor.GRAY))
    MinecraftServer.getInstanceManager().instances.forEach { if (it is InstanceContainer) it.saveInstance() }
}

fun stop() {
    if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            addProperty("content", ":red_circle: **Server stopped!**")
        })
    MinecraftServer.stopCleanly()
    exitProcess(0)
}

fun broadcast(message: Component) {
    Audiences.server().sendMessage(message)
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

val webhookUrl = getProperty("webhook-url")
//const val WEBHOOK_URL = "https://discord.com/api/webhooks/871724748818763836/a_T9R18nU51xMmWjUIYAZgY1kOvuWaaJHEzUu45mDEOwoEKnZmAr_k6hUSIP4rORCK6T"

val faviconBase64 = loadImageBase64("./server-icon.png")?.let { "data:image/png;base64,$it" }