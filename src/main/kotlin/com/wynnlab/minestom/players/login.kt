package com.wynnlab.minestom.players

import com.wynnlab.minestom.io.HttpRequestException
import com.wynnlab.minestom.io.getApiResultsJson
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.UuidProvider
import net.minestom.server.network.player.PlayerConnection
import java.util.*
import java.util.function.Consumer

object WynnLabUuidProvider : UuidProvider {
    override fun provide(connection: PlayerConnection?, username: String?): UUID {
        val response = try {
            getApiResultsJson("https://api.mojang.com/users/profiles/minecraft/$username")
        } catch (e: HttpRequestException) {
            MinecraftServer.LOGGER.warn("Could not get UUID for player $username (${e.responseCode})")
            return UUID.randomUUID()
        }
        val id = response.get("id").asString
        val uuid = UUID(id.substring(0, 16).toULong(16).toLong(), id.substring(16).toULong(16).toLong())
        MinecraftServer.LOGGER.info("Player $username logged in with UUID $uuid")
        return uuid
    }
}

class WynnLabLogin(private val spawningInstance: Instance) : Consumer<PlayerLoginEvent> {
    override fun accept(e: PlayerLoginEvent) {
        e.setSpawningInstance(spawningInstance)

        val player = e.player

        player.gameMode = GameMode.ADVENTURE
        player.food = 20
        player.foodSaturation = 0f
        player.exp = 0f
        player.level = 106

        prepareInventory(player)
    }
}