package com.wynnlab.minestom.gui

import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.hideAllFlags
import com.wynnlab.minestom.util.loreNonItalic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.StackingRule

class MenuGui : Gui("§c200 §4Skill Points Remaining", InventoryType.CHEST_3_ROW) {
    private val strBookItem = abilityBookItem(0, Component.text("Strength", NamedTextColor.DARK_GREEN))
    private val dexBookItem = abilityBookItem(1, Component.text("Dexterity", NamedTextColor.YELLOW))
    private val intBookItem = abilityBookItem(2, Component.text("Intelligence", NamedTextColor.AQUA))
    private val defBookItem = abilityBookItem(3, Component.text("Defense", NamedTextColor.RED))
    private val agiBookItem = abilityBookItem(4, Component.text("Agility", NamedTextColor.WHITE))

    private val guildBannerItem = guildBannerItem()

    override fun initItems() {
        inv.setItemStack(4, resetSkillPointsItem)
        inv.setItemStack(0, tomesItem)
        inv.setItemStack(18, settingsItem)
    }

    override fun updateItems() {
        inv.setItemStack(11, strBookItem.build())
        inv.setItemStack(12, dexBookItem.build())
        inv.setItemStack(13, intBookItem.build())
        inv.setItemStack(14, defBookItem.build())
        inv.setItemStack(15, agiBookItem.build())

        inv.setItemStack(9, guildBannerItem)
    }

private var i = 1
    override fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult) {
        result.isCancel = true

        val amount = when (clickType) {
            ClickType.LEFT_CLICK -> 1
            ClickType.RIGHT_CLICK -> -1
            else -> 0
        }
        when (slot) {
            in 11..15 -> result.clickedItem = strBookItem.amount(run { i += amount; i }).build()
        }

        inv.title = Component.text()
            .append(Component.text(200 - i, NamedTextColor.RED))
            .append(Component.text(" Skill Points Remaining", NamedTextColor.DARK_RED))
            .build()
    }
}

private val resetSkillPointsItem = ItemStack.builder(Material.GOLDEN_SHOVEL)
    .displayNameNonItalic(Component.text("Reset Skill Points", NamedTextColor.YELLOW))
    .loreNonItalic(Component.text()
        .append(Component.text("Cost: ", NamedTextColor.GRAY))
        .append(Component.text("0 Soul Points", NamedTextColor.WHITE))
        .build())
    .meta { it
        .damage(21)
        .unbreakable(true)
        .hideAllFlags()
    }
    .build()

private val tomesItem = ItemStack.builder(Material.ENCHANTED_BOOK)
    .displayNameNonItalic(Component.text("Mastery Tomes", Style.style(NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)))
    .build()

private fun guildBannerItem() = ItemStack.builder(Material.BLACK_BANNER)
    .displayNameNonItalic(Component.text("View Your Guild", Style.style(NamedTextColor.AQUA, TextDecoration.BOLD)))
    .build()

private val settingsItem = ItemStack.builder(Material.CRAFTING_TABLE)
    .displayNameNonItalic(Component.text("WynnLab Settings"))
    .build()

private val jukeboxItem = ItemStack.builder(Material.JUKEBOX)
    .displayNameNonItalic(Component.text("Jukebox", NamedTextColor.AQUA))
    .build()


private fun abilityBookItem(index: Int, name: Component) = ItemStack.builder(Material.BOOK)
    .displayNameNonItalic(Component.text()
        .append(Component.text("Upgrade your ", NamedTextColor.LIGHT_PURPLE))
        .append(name)
        .append(Component.text(" skill", NamedTextColor.LIGHT_PURPLE))
        .build())