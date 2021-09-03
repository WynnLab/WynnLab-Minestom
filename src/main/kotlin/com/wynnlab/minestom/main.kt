@file:JvmName("Main")

package com.wynnlab.minestom

import com.wynnlab.minestom.commands.*
import com.wynnlab.minestom.core.player.initActionBar
import com.wynnlab.minestom.core.runIdTasks
import com.wynnlab.minestom.discord.initDiscordClient
import com.wynnlab.minestom.discord.postLogWebhook
import com.wynnlab.minestom.generator.GeneratorDemo
import com.wynnlab.minestom.gui.guiEventNode
import com.wynnlab.minestom.items.ItemCommand
import com.wynnlab.minestom.labs.registerLabsCommands
import com.wynnlab.minestom.labs.registerLabsListeners
import com.wynnlab.minestom.listeners.initServerListeners
import com.wynnlab.minestom.listeners.initWynnLabListeners
import com.wynnlab.minestom.mob.MobCommand
import com.wynnlab.minestom.players.WynnLabLogin
import com.wynnlab.minestom.players.WynnLabUuidProvider
import com.wynnlab.minestom.util.listen
import com.wynnlab.minestom.util.loadImageBase64
import kotlinx.serialization.json.put
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.lan.OpenToLAN
import net.minestom.server.extras.lan.OpenToLANConfig
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.ping.ServerListPingType
import net.minestom.server.utils.time.TimeUnit
import kotlin.random.Random
import kotlin.system.exitProcess

fun main() {
    val server = MinecraftServer.init()

    val oldExceptionManager = MinecraftServer.getExceptionManager()
    MinecraftServer.getExceptionManager().setExceptionHandler {
        oldExceptionManager.handleException(it)
        postLogWebhook {
            put("content", "Exception:\n```$it```")
        }
    }


    MinecraftServer.setBrandName("WynnLab")

    val connectionManager = MinecraftServer.getConnectionManager()
    connectionManager.setUuidProvider(WynnLabUuidProvider)

    //val storageManager = MinecraftServer.getStorageManager()
    //storageManager.defineDefaultStorageSystem()
    //val storageLocation = storageManager.getLocation("world")

    val schedulerManager = MinecraftServer.getSchedulerManager()
    schedulerManager.buildShutdownTask {
        postLogWebhook {
            put("content", "> Server stopped :red_circle:")
        }
    }.makeTransient().schedule()
    schedulerManager.buildShutdownTask(::saveAll).makeTransient().schedule()
    schedulerManager.buildTask(::saveAll).delay(5, TimeUnit.MINUTE).repeat(5, TimeUnit.MINUTE).schedule()
    runIdTasks()

    initActionBar()

    val instanceManager = MinecraftServer.getInstanceManager()
    mainInstance = instanceManager.createInstanceContainer()
    mainInstance.chunkGenerator = GeneratorDemo()

    val commandManager = MinecraftServer.getCommandManager()
    registerServerCommands(commandManager)

    commandManager.register(HelpCommand)
    commandManager.register(ClassCommand)
    commandManager.register(CastCommand)
    commandManager.register(DummyCommand)
    commandManager.register(MenuCommand)
    registerPvpCommands(commandManager)
    registerEssentialsCommands(commandManager)
    registerDebugCommands(commandManager)

    registerLabsCommands(commandManager)
    commandManager.register(ItemCommand)
    commandManager.register(MobCommand)

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.listen<PlayerLoginEvent> { event ->
        event.player.respawnPoint = Pos(0.0, 42.0, 0.0)
        event.player.permissionLevel = 4
    }
    globalEventHandler.addListener(PlayerLoginEvent::class.java, WynnLabLogin(mainInstance))

    globalEventHandler.listen<ServerListPingEvent> { event ->
        val responseData = event.responseData
        responseData.version = "WynnLab ${MinecraftServer.VERSION_NAME}"
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
    registerLabsListeners(globalEventHandler)

    globalEventHandler.addChild(guiEventNode)

    val ip = getProperty("server-ip", "0.0.0.0")
    val port = getProperty("server-port", "25565").toIntOrNull() ?: 25565
    server.start(ip, port)

    if (getProperty("open-to-lan") == "true")
        OpenToLAN.open(OpenToLANConfig())

    postLogWebhook {
        put("content", "> Server started :green_circle:")
    }
    initDiscordClient()
}

lateinit var mainInstance: InstanceContainer

fun saveAll() {
    Audiences.players().sendMessage(Component.translatable("server.saving", NamedTextColor.GRAY))
    //MinecraftServer.getInstanceManager().instances.forEach { if (it is InstanceContainer /*&& it.storageLocation != null*/) it.saveInstance(); it.saveChunksToStorage() }
    //TODO: saving
}

fun stop() {
    /*if (webhookUrl != null)
        post(webhookUrl, JsonObject().apply {
            addProperty("content", ":red_circle: **Server stopped!**")
        })*/
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

val faviconBase64 = loadImageBase64("./server-icon.png")?.let { "data:image/png;base64,$it" }

val random = Random(System.currentTimeMillis())