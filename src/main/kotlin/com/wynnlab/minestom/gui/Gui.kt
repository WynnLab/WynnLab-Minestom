@file:Suppress("LeakingThis")

package com.wynnlab.minestom.gui

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult

abstract class Gui(
    title: String,
    type: InventoryType
) {
    protected val inv = Inventory(type, title)
    private val closeListener = EventListener.of(InventoryCloseEvent::class.java, this::onClose)
    init {
        initItems()
        inv.addInventoryCondition(this::onClick)
        MinecraftServer.getGlobalEventHandler().addListener(closeListener)
    }

    fun show(player: Player) {
        onOpen(player)
        player.openInventory(inv)
    }

    protected abstract fun initItems()

    protected open fun onOpen(player: Player) = Unit

    protected abstract fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult)

    private fun onClose(e: InventoryCloseEvent) {
        onClose(e.player)
        MinecraftServer.getGlobalEventHandler().removeListener(closeListener)
    }

    protected open fun onClose(player: Player) = Unit
}