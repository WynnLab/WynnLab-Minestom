package com.wynnlab.minestom

import com.wynnlab.minestom.generator.GeneratorDemo
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.utils.Position

fun main() {
    val server = MinecraftServer.init()

    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    instanceContainer.chunkGenerator = GeneratorDemo()

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Position(.0, 42.0, .0)
        player.gameMode = GameMode.CREATIVE
        player.permissionLevel = 4
    }

    globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
        val responseData = event.responseData
        responseData.version = "WynnLab 1.17"
        responseData.description = Component.text("WynnLab Minestom")
    }

    server.start("0.0.0.0", 25565)
}