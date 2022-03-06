package com.wynnlab.minestom.gui

import com.wynnlab.minestom.base.playerClassTag
import com.wynnlab.minestom.base.playerCloneClassTag
import com.wynnlab.minestom.classes.classes
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult
import net.minestom.server.sound.SoundEvent

class ClassGui : Gui("Choose a Class!", InventoryType.CHEST_1_ROW) {
    override fun initItems() {
        for ((i, clazz) in classes.withIndex())
            inv.setItemStack(i, clazz.item)
    }

    override fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult) {
        result.isCancel = true

        val cloneClass = when (clickType) {
            ClickType.LEFT_CLICK -> false
            ClickType.RIGHT_CLICK -> true
            else -> return
        }

        val selectedClass = classes.getOrNull(slot) ?: return

        player.setTag(playerClassTag, selectedClass.id)
        player.setTag(playerCloneClassTag, cloneClass)

        player.closeInventory()
        player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1f, 1f))
    }
}