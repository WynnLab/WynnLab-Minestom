@file:Suppress("LeakingThis")

package com.wynnlab.minestom.gui

import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult

abstract class Gui(
    title: String,
    type: InventoryType
) {
    protected val inv = Inventory(type, title)
    init {
        initItems()
        inv.addInventoryCondition(this::onClick)
    }

    fun show(player: Player) {
        update()
        player.openInventory(inv)
    }

    fun update() = updateItems()

    protected abstract fun initItems()

    protected open fun updateItems() = Unit

    protected abstract fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult)
}