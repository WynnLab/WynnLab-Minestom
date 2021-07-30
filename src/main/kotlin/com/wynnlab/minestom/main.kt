@file:JvmName("Main")

package com.wynnlab.minestom

import com.wynnlab.minestom.commands.*
import com.wynnlab.minestom.generator.GeneratorDemo
import com.wynnlab.minestom.io.HttpRequestException
import com.wynnlab.minestom.io.getApiResultsJson
import com.wynnlab.minestom.listeners.initServerListeners
import com.wynnlab.minestom.listeners.initWynnLabListeners
import com.wynnlab.minestom.util.listen
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.other.ArmorStandMeta
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.lan.OpenToLAN
import net.minestom.server.extras.lan.OpenToLANConfig
import net.minestom.server.network.packet.server.play.EntityAnimationPacket
import net.minestom.server.ping.ServerListPingType
import net.minestom.server.utils.Position
import java.util.*

fun main() {
    val server = MinecraftServer.init()

    MinecraftServer.setBrandName("WynnLab")

    val connectionManager = MinecraftServer.getConnectionManager()
    connectionManager.setUuidProvider { _, username ->
        val response = try {
            getApiResultsJson("https://api.mojang.com/users/profiles/minecraft/$username")
        } catch (e: HttpRequestException) {
            MinecraftServer.LOGGER.warn("Could not get UUID for player $username (${e.responseCode})")
            return@setUuidProvider UUID.randomUUID()
        }
        val id = response.get("id").asString
        val uuid = UUID(id.substring(0, 16).toULong(16).toLong(), id.substring(16).toULong(16).toLong())
        MinecraftServer.LOGGER.info("Player $username logged in with UUID $uuid")
        uuid
    }

    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    instanceContainer.chunkGenerator = GeneratorDemo()

    val commandManager = MinecraftServer.getCommandManager()
    registerServerCommands(commandManager)

    commandManager.register(ClassCommand)
    commandManager.register(CastCommand)
    commandManager.register(ItemCommand)
    commandManager.register(DummyCommand)
    registerPvpCommands(commandManager)
    registerEssentialsCommands(commandManager)

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.listen<PlayerLoginEvent> { event ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Position(0.0, 42.0, 0.0)
        player.permissionLevel = 4
    }

    globalEventHandler.listen<ServerListPingEvent> { event ->
        val responseData = event.responseData
        responseData.version = "WynnLab (1.17)"
        responseData.description = when (event.pingType) {
            ServerListPingType.MODERN_FULL_RGB -> motd
            ServerListPingType.OPEN_TO_LAN -> lanMotd
            else -> legacyMotd
        }//if (event.pingType == ServerListPingType.MODERN_FULL_RGB) motd else legacyMotd
    }


    initServerListeners(globalEventHandler)
    initWynnLabListeners(globalEventHandler)

    server.start("0.0.0.0", 25565)

    OpenToLAN.open(OpenToLANConfig())

    /*commandManager.register(object : net.minestom.server.command.builder.Command("anim") {
        init {
            setCondition { sender, _ -> sender.isPlayer }
            val ordinalArg = net.minestom.server.command.builder.arguments.ArgumentType.Integer("ordianl").between(0, 5)
            addSyntax({ sender, ctx ->
                sender as Player
                val anim = EntityAnimationPacket.Animation.values()[ctx[ordinalArg]]
                sender.playerConnection.sendPacket(EntityAnimationPacket().also { it.entityId = sender.entityId; it.animation = anim })
                sender.sendMessage("Playing animation $anim")
            }, ordinalArg)
        }
    })*/
    /*commandManager.register(object : net.minestom.server.command.builder.Command("sp") {
        init {
            setCondition { sender, _ -> sender.isPlayer }
            val spArg = net.minestom.server.command.builder.arguments.ArgumentType.Boolean("spectate")
            addSyntax({ sender, ctx ->
                sender as Player
                if (ctx[spArg]) {
                    val a = Entity(EntityType.ARMOR_STAND)
                    val m = a.entityMeta as ArmorStandMeta
                    m.setNotifyAboutChanges(false)
                    m.isMarker = true
                    m.isInvisible = true
                    m.isHasNoGravity = true
                    m.setNotifyAboutChanges(true)
                    a.setInstance(sender.instance!!, sender.position.add(sender.position.direction.multiply(-2).toPosition()))
                    sender.spectate(a)
                    sender.isInvisible = true
                } else {
                    sender.stopSpectating()
                    sender.isInvisible = false
                }
            }, spArg)
        }
    })*/
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