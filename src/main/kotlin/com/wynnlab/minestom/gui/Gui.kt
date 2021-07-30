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
    private val inv = Inventory(type, title)
    init {
        initItems(inv)
        inv.addInventoryCondition(this::onClick)
    }

    fun show(player: Player) {
        update()
        player.openInventory(inv)
    }

    fun update() = updateItems(inv)

    protected abstract fun initItems(inv: Inventory)

    protected open fun updateItems(inv: Inventory) = Unit

    protected abstract fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult)
}