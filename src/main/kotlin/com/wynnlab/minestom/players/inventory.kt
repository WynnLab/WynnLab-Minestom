package com.wynnlab.minestom.players

import com.wynnlab.minestom.*
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.loreNonItalic
import com.wynnlab.minestom.util.setIfEmpty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.WrittenBookMeta

fun prepareInventory(player: Player) {
    val inv = player.inventory

    inv.setItemStack(6, compassItem)
    inv.setItemStack(7, questBookItem)
    inv.setItemStack(8, soulPointsItem)

    inv.setIfEmpty(9, snowRing1)
    inv.setIfEmpty(10, snowRing2)
    inv.setIfEmpty(11, snowBracelet)
    inv.setIfEmpty(12, snowNecklace)

    inv.setIfEmpty(13, emptyPouch)
}

private val compassItem = ItemStack.builder(Material.COMPASS)
    .displayNameNonItalic(Component.translatable("inventory.compass", COLOR_LIGHT_BLUE))
    .loreNonItalic(Component.translatable("inventory.compass.lore", COLOR_PALE_BLUE))
    .build()

private val questBookItem = ItemStack.builder(Material.WRITTEN_BOOK)
    .displayNameNonItalic(Component.translatable("inventory.quest_book", NamedTextColor.LIGHT_PURPLE))
    .loreNonItalic(Component.translatable("inventory.quest_book.quests", COLOR_PURPLE, Component.text()
        .append(Component.text("0/0", COLOR_PINK))
        .append(Component.text( " [", COLOR_PURPLE))
        .append(Component.text("100%", COLOR_PINK))
        .append(Component.text("]", COLOR_PURPLE))
        .build()))
    .meta(WrittenBookMeta::class.java) { it
        .author("WynnLab")
        .generation(WrittenBookMeta.WrittenBookGeneration.ORIGINAL)
    }
    .build()

private val soulPointsItem = ItemStack.builder(Material.NETHER_STAR)
    .displayName(Component.translatable("inventory.soul_points", Style.style(COLOR_PALE_AQUA_BLUE, TextDecoration.BOLD), Component.text("15", NamedTextColor.YELLOW)))
    .loreNonItalic(
        Component.translatable("inventory.soul_points.lore.1", NamedTextColor.GRAY),
        Component.translatable("inventory.soul_points.lore.2", NamedTextColor.GRAY),
        Component.translatable("inventory.soul_points.lore.3", NamedTextColor.GRAY),
    )
    .build()


val emptyPouch = ItemStack.builder(Material.BUNDLE)
    .displayNameNonItalic(Component.translatable("inventory.pouch", COLOR_GOLD))
    .loreNonItalic(
        Component.translatable("inventory.pouch.view", NamedTextColor.GRAY, Component.keybind("key.attack")),
        Component.translatable("inventory.pouch.clear", NamedTextColor.GRAY, Component.keybind("key.use")),
    )
    .build()


private val snowRing1 = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.translatable("inventory.slot.ring", NamedTextColor.GRAY).append(Component.text("", NamedTextColor.BLACK)))
    .build()
private val snowRing2 = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.translatable("inventory.slot.ring", NamedTextColor.GRAY).append(Component.text("", NamedTextColor.DARK_BLUE)))
    .build()
private val snowBracelet = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.translatable("inventory.slot.bracelet", NamedTextColor.GRAY))
    .build()
private val snowNecklace = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.translatable("inventory.slot.necklace", NamedTextColor.GRAY))
    .build()

fun snowForSlot(slot: Int) = when (slot) {
    9 -> snowRing1
    10 -> snowRing2
    11 -> snowBracelet
    else -> snowNecklace
}