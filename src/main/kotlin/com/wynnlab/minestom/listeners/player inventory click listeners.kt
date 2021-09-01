package com.wynnlab.minestom.listeners

import com.wynnlab.minestom.core.player.checkPlayerItems
import com.wynnlab.minestom.core.player.modifiedSkills
import com.wynnlab.minestom.items.ItemTypeId
import com.wynnlab.minestom.items.itemTypeIdTag
import com.wynnlab.minestom.items.skillRequirementsTag
import com.wynnlab.minestom.players.snowForSlot
import com.wynnlab.minestom.util.listen
import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

val playerInventoryClickListenersNode = EventNode.event("player-inventory-click-listeners", EventFilter.PLAYER) { e ->
    e is InventoryPreClickEvent && e.inventory == null && e.player.gameMode == GameMode.ADVENTURE
}

private fun onPlayerInventoryClick(e: InventoryPreClickEvent) {
    val player = e.player
    if (e.slot in 6..8 && e.clickType != ClickType.START_SHIFT_CLICK) {
        e.isCancelled = true
        return
    }
    if (e.slot == 13) {
        e.isCancelled = true
        return
    }
    if (e.slot == 45 || e.clickType == ClickType.CHANGE_HELD) { // Disable offhand
        e.isCancelled = true
        return
    }
    /*println("""
        =====
        Slot: ${e.slot}
        Type: ${e.clickType}
        Cursor: ${e.cursorItem.material}
        Clicked: ${e.clickedItem.material}
    """.trimIndent())*/
    run clickType@ {
        when (e.clickType) {
            ClickType.START_SHIFT_CLICK -> {
                if (e.slot in 5..8) checkPlayerItems(player)
                if (e.slot in 9..12) {
                    player.inventory.setItemStack(e.slot, snowForSlot(e.slot))
                    e.isCancelled = true
                    if (!player.inventory.addItemStack(e.clickedItem)) e.isCancelled = false
                    if (e.isCancelled) checkPlayerItems(player)
                    return
                } else if (e.slot in 0..8) return
                val shiftTo = when (e.clickedItem.getTag(itemTypeIdTag)) {
                    null -> return@clickType
                    ItemTypeId.HELMET -> 41
                    ItemTypeId.CHESTPLATE -> 42
                    ItemTypeId.LEGGINGS -> 43
                    ItemTypeId.BOOTS -> 44
                    ItemTypeId.RING -> if (player.inventory.getItemStack(9).material == Material.SNOW) 9 else 10
                    ItemTypeId.BRACELET -> 11
                    ItemTypeId.NECKLACE -> 12
                    else -> return@clickType
                }
                if (!checkItem(shiftTo, e.clickedItem, player.modifiedSkills)) {
                    if (shiftTo >= 41) e.isCancelled = true
                } else if (player.inventory.getItemStack(shiftTo).let { it.isAir || it.material == Material.SNOW }) {
                    player.inventory.setItemStack(shiftTo, e.clickedItem)
                    player.inventory.setItemStack(e.slot.let { if (it >= 36) it - 36 else it }, ItemStack.AIR)
                    e.isCancelled = true
                    checkPlayerItems(player)
                }
            }
            ClickType.SHIFT_CLICK -> return
            else -> return@clickType
        }
    }
    if (e.slot in 9..12 || e.slot in 41..44) {
        if (e.cursorItem.isAir) {
            if (e.slot < 41) {
                if (e.clickedItem.material != Material.SNOW)
                    e.cursorItem = snowForSlot(e.slot)
                else
                    e.isCancelled = true
            }
        } else {
            if (!checkItem(e.slot, e.cursorItem, player.modifiedSkills))
                e.isCancelled = true
            else if (e.clickedItem.material == Material.SNOW)
                e.clickedItem = ItemStack.AIR
        }
        if (!e.isCancelled) checkPlayerItems(player)
    }
}

private fun checkItem(slot: Int, item: ItemStack, modifiedSkills: IntArray): Boolean {
    if (item.getTag(itemTypeIdTag) != when (slot) {
        41 -> ItemTypeId.HELMET
        42 -> ItemTypeId.CHESTPLATE
        43 -> ItemTypeId.LEGGINGS
        44 -> ItemTypeId.BOOTS
        9, 10 -> ItemTypeId.RING
        11 -> ItemTypeId.BRACELET
        12 -> ItemTypeId.NECKLACE
        else -> return false
    }.toByte()) return false
    val skillRequirements = item.getTag(skillRequirementsTag) ?: return false
    for (i in 0..4) {
        if (modifiedSkills[i] < skillRequirements[i]) return false
    }
    return true
}

fun initPlayerInventoryClickListeners() {
    //playerSpellClickListenersNode.listen {  }
    playerInventoryClickListenersNode.listen(::onPlayerInventoryClick)

    wynnLabPlayerListenersNode.addChild(playerInventoryClickListenersNode)
}