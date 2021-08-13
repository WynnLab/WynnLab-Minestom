package com.wynnlab.minestom.gui

import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.sound.SoundEvent

class ClassGui : Gui("Choose a Class!", InventoryType.CHEST_1_ROW) {
    override fun initItems() {
        inv.setItemStack(1, ItemStack.of(Material.STICK))
        inv.setItemStack(2, ItemStack.of(Material.STONE))
    }

    override fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult) {
        result.isCancel = true

        when (clickType) {
            ClickType.LEFT_CLICK -> player.sendMessage("LC $slot")
            ClickType.RIGHT_CLICK -> player.sendMessage("RC $slot")
            else -> return
        }

        player.closeInventory()
        player.playSound(Sound.sound(SoundEvent.PLAYER_LEVELUP, Sound.Source.MASTER, 1f, 1f))
    }
}