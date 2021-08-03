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
    .displayNameNonItalic(Component.text("Character Info", COLOR_LIGHT_BLUE.textColor))
    .loreNonItalic(Component.text("View and manage your skills", COLOR_PALE_BLUE.textColor))
    .build()

private val questBookItem = ItemStack.builder(Material.WRITTEN_BOOK)
    .displayNameNonItalic(Component.text("Quest Book", NamedTextColor.LIGHT_PURPLE))
    .loreNonItalic(Component.text()
        .append(Component.text("Quests: ", COLOR_PURPLE.textColor))
        .append(Component.text("0/0", COLOR_PINK.textColor))
        .append(Component.text( " [", COLOR_PURPLE.textColor))
        .append(Component.text("100%", COLOR_PINK.textColor))
        .append(Component.text("]", COLOR_PURPLE.textColor))
        .build())
    .meta(WrittenBookMeta::class.java) { it
        .author("WynnLab")
        .generation(WrittenBookMeta.WrittenBookGeneration.ORIGINAL)
    }
    .build()

private val soulPointsItem = ItemStack.builder(Material.NETHER_STAR)
    .displayName(Component.text()
        .append(Component.text("15", NamedTextColor.YELLOW))
        .append(Component.text(" Soul Points", Style.style(COLOR_PALE_AQUA_BLUE.textColor, TextDecoration.BOLD)))
        .build())
    .loreNonItalic(
        Component.text("Having less soul points increases", NamedTextColor.GRAY),
        Component.text("the chance of dropping items upon", NamedTextColor.GRAY),
        Component.text("death.", NamedTextColor.GRAY),
    )
    .build()


val emptyPouch = ItemStack.builder(Material.BUNDLE)
    .displayNameNonItalic(Component.text("Magic Pouch", COLOR_GOLD.textColor))
    .loreNonItalic(
        Component.text().append(Component.text("Left-Click", NamedTextColor.WHITE)).append(Component.text(" to view contents", NamedTextColor.GRAY)).build(),
        Component.text().append(Component.text("Right-Click", NamedTextColor.WHITE)).append(Component.text(" to clear", NamedTextColor.GRAY)).build(),
    )
    .build()


private val snowRing1 = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.text("Ring Slot", NamedTextColor.GRAY).append(Component.text("", NamedTextColor.BLACK)))
    .build()
private val snowRing2 = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.text("Ring Slot", NamedTextColor.GRAY).append(Component.text("", NamedTextColor.DARK_BLUE)))
    .build()
private val snowBracelet = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.text("Bracelet Slot", NamedTextColor.GRAY))
    .build()
private val snowNecklace = ItemStack.builder(Material.SNOW)
    .displayNameNonItalic(Component.text("Necklace Slot", NamedTextColor.GRAY))
    .build()

fun snowForSlot(slot: Int) = when (slot) {
    9 -> snowRing1
    10 -> snowRing2
    11 -> snowBracelet
    else -> snowNecklace
}