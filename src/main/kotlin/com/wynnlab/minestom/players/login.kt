package com.wynnlab.minestom.players

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.wynnlab.minestom.util.get
import kotlinx.coroutines.*
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.UuidProvider
import net.minestom.server.network.player.PlayerConnection
import net.minestom.server.tag.Tag
import java.util.*
import java.util.function.Consumer

object WynnLabUuidProvider : UuidProvider {
    override fun provide(connection: PlayerConnection?, username: String?): UUID = runBlocking {
        val response = try {
            get("https://api.mojang.com/users/profiles/minecraft/$username")
        } catch (e: Exception) {
            MinecraftServer.LOGGER.warn("Could not get UUID for player $username (${e::class.java.simpleName})")
            return@runBlocking UUID.randomUUID()
        }.await()
        val id = response["id"]!!.jsonPrimitive.content
        val uuid = UUID(id.substring(0, 16).toULong(16).toLong(), id.substring(16).toULong(16).toLong())
        MinecraftServer.LOGGER.info("Player $username logged in with UUID $uuid")
        return@runBlocking uuid
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

        GlobalScope.launch {
            setWynnLabName(player)
        }
    }
}

private suspend fun setWynnLabName(player: Player) {
    val data = getAPIDataAsync(player).await()

    val name = Component.text()
        //.append(Component.text("[106/Cl", NamedTextColor.GRAY))

    val rank = if (data == null) null else when (data["rank"]!!.jsonPrimitive.content) {
        "Player" -> when (data["meta"]!!.jsonObject["tag"]!!.jsonObject["value"]!!.jsonPrimitive.content) {
            "VIP" -> Rank.Vip
            "VIP+" -> Rank.`Vip+`
            "HERO" -> Rank.Hero
            "CHAMPION" -> Rank.Champion
            else -> Rank.Player
        }
        "Administrator" -> Rank.Admin
        "Moderator" -> Rank.Mod
        "Media" -> Rank.Media
        else -> Rank.CT
    }

    /*val guildTag = if (data == null) null else loadGuildData(player, data)
    if (guildTag != null) {
        name.append(Component.text("/$guildTag", NamedTextColor.GRAY))
    }
    name.append(Component.text("] ", NamedTextColor.GRAY))*/

    if (rank?.tag != null) {
        name.append(rank.tag)
        name.append(Component.text(" "))
    }

    name.append(Component.text(player.username, rank?.nameColor ?: NamedTextColor.GRAY))

    player.displayName = name.build()
    player.customName = player.displayName
    player.isCustomNameVisible = true
}

private suspend fun getAPIDataAsync(player: Player) = coroutineScope { async {
    try {
        val root = get("https://api.wynncraft.com/v2/player/${player.username}/stats").await()
        val data = try {
            root["data"]!!.jsonArray[0].jsonObject
        } catch (_: IndexOutOfBoundsException) {
            return@async null
        }
        data
    } catch (_: Exception) {
        null
    }
} }

private suspend fun loadGuildData(player: Player, data: JsonObject): String? {
    val guildData = data.getAsJsonObject("guild") ?: return null
    val guildName = guildData["name"].takeUnless { it is JsonNull }?.asString
    val guildRank = guildData["rank"].takeUnless { it is JsonNull }?.asString

    var guildTag: String? = null
    if (guildName != null) {
        val guild =
            get("https://api.wynncraft.com/public_api.php?action=guildStats&command=${guildName.replace(" ", "%20")}").await()
        guildTag = guild["prefix"]!!.jsonPrimitive.content

        player.setTag(Tag.String("guild_name"), guildName)
        player.setTag(Tag.String("guild_tag"), guildTag)
    }
    if (guildRank != null) player.setTag(Tag.String("guild_rank"), guildRank)

    return guildTag
}
