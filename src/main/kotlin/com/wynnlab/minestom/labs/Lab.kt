package com.wynnlab.minestom.labs

import com.wynnlab.minestom.generator.GeneratorDemo
import com.wynnlab.minestom.items.ItemBuilder
import com.wynnlab.minestom.mainInstance
import com.wynnlab.minestom.players.prepareInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.inventory.itemStacksRaw
import net.minestom.server.item.ItemStack
import net.minestom.server.utils.Position
import net.minestom.server.world.DimensionType
import java.util.*
import kotlin.collections.HashMap

class Lab(owner: Player) : InstanceContainer(owner.uuid, DimensionType.OVERWORLD, null) {
    init {
        MinecraftServer.LOGGER.info("Starting lab...")
        MinecraftServer.getInstanceManager().registerInstance(this)

        chunkGenerator = GeneratorDemo()

        worldBorder.diameter = 127.0
    }

    val itemBuilders = HashMap<String, ItemBuilder>()

    fun owns(player: Player) = uniqueId == player.uuid

    fun join(player: Player) {
        player.setInstance(this, Position(.0, 42.0, .0))
        player.scheduleNextTick {
            (it as Player).playerConnection.sendPacket(MinecraftServer.getCommandManager().createDeclareCommandsPacket(it))
        }
        oldInventories[player.uuid] = player.inventory.itemStacks
        player.inventory.itemStacksRaw.fill(ItemStack.AIR)
        prepareInventory(player)

        //player.sendMessage(Component.text("${player.username} joined the lab", NamedTextColor.YELLOW)) // Yes I know
        val name = player.username
        scheduleNextTick {
            it.sendMessage(Component.text("$name joined the lab", NamedTextColor.YELLOW))
        }
    }

    fun leave(player: Player) {
        sendMessage(Component.text("${player.username} left the lab", NamedTextColor.YELLOW))

        oldInventories[player.uuid]?.let { i ->
            System.arraycopy(i, 0, player.inventory.itemStacksRaw, 0, i.size)
            player.inventory.update()
        }
        player.setInstance(mainInstance, Position(.0, 42.0, .0))

        if (players.isEmpty()) {
            delete()
            return
        }

        if (player.uuid == uniqueId) {
            val newOwner = players.maxByOrNull(::getPermissions)!!
            sendMessage(Component.text("The owner has left the lab. ${newOwner.username} is the new owner"))
            transfer(newOwner)
        }
    }

    fun transfer(newOwner: Player) {
        val oldOwner = uniqueId

        //MinecraftServer.getInstanceManager().unregisterInstance(this)
        uniqueId = newOwner.uuid
        //MinecraftServer.getInstanceManager().registerInstance(this)
        println(MinecraftServer.getInstanceManager().getInstance(uniqueId) == this)

        players.find { it.uuid == oldOwner }?.let {
            it.playerConnection.sendPacket(MinecraftServer.getCommandManager().createDeclareCommandsPacket(it))
        }
        newOwner.playerConnection.sendPacket(MinecraftServer.getCommandManager().createDeclareCommandsPacket(newOwner))
    }

    private fun delete() {
        MinecraftServer.LOGGER.info("Shutting down lab...")
        MinecraftServer.getInstanceManager().unregisterInstance(this)
    }

    private val permissions = hashMapOf<UUID, Int>()

    fun getPermissions(player: Player) = if (player.uuid == uniqueId) 3 else permissions[player.uuid] ?: 0

    fun setPermissions(player: Player, level: Int) { permissions[player.uuid] = level }

    private val oldInventories = hashMapOf<UUID, Array<ItemStack>>()
}

fun getLab(owner: Player) = MinecraftServer.getInstanceManager().getInstance(owner.uuid) as? Lab?