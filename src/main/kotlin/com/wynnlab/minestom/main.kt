package com.wynnlab.minestom

import com.wynnlab.minestom.commands.ClassCommand
import com.wynnlab.minestom.commands.PermissionCommand
import com.wynnlab.minestom.commands.StopCommand
import com.wynnlab.minestom.generator.GeneratorDemo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.utils.Position

fun main() {
    val server = MinecraftServer.init()

    MinecraftServer.setBrandName("WynnLab")

    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    instanceContainer.chunkGenerator = GeneratorDemo()

    val commandManager = MinecraftServer.getCommandManager()
    commandManager.register(StopCommand)
    commandManager.register(PermissionCommand)

    commandManager.register(ClassCommand)

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
        responseData.description = motd
    }

    server.start("0.0.0.0", 25565)
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