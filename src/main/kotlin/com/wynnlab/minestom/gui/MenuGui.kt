package com.wynnlab.minestom.gui

import com.wynnlab.minestom.COLOR_WYNN
import com.wynnlab.minestom.core.player.*
import com.wynnlab.minestom.textColor
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.hideAllFlags
import com.wynnlab.minestom.util.loreNonItalic
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult
import net.minestom.server.item.*
import net.minestom.server.sound.SoundEvent
import java.util.*
import kotlin.math.roundToInt

object MenuGui : Gui("§c200 §4Skill Points Remaining", InventoryType.CHEST_3_ROW) {
    private fun getTitle(player: Player) = Component.translatable("gui.menu.skill_points_remaining", NamedTextColor.DARK_RED,
        Component.text(player.remainingSkillAssigns, NamedTextColor.RED))

    private val guildBannerItem = guildBannerItem()

    override fun initItems() {
        inv.setItemStack(4, resetSkillPointsItem)
        inv.setItemStack(0, tomesItem)
        inv.setItemStack(18, settingsItem)
        inv.setItemStack(22, jukeboxItem)
    }

    override fun onOpen(player: Player) {
        inv.title = getTitle(player)

        refreshSkillBooks(player)

        inv.setItemStack(9, guildBannerItem)
    }

    private fun refreshSkillBooks(player: Player) {
        val s = player.modifiedSkills
        currentSkills[player.uuid] = s
        val a = player.assignedSkills
        var m = 0
        for (i in 0..4) {
            if (s[i] != player.assignedSkill(i, a).toInt()) m += 1 shl i
        }
        currentSkillsModified[player.uuid] = m

        inv.setItemStack(11, strBookItem.skillBookAmount(m and 0b1 > 0, s[0]).build())
        inv.setItemStack(12, dexBookItem.skillBookAmount(m and 0b10 > 0, s[1]).build())
        inv.setItemStack(13, intBookItem.skillBookAmount(m and 0b100 > 0, s[2]).build())
        inv.setItemStack(14, defBookItem.skillBookAmount(m and 0b1000 > 0, s[3]).build())
        inv.setItemStack(15, agiBookItem.skillBookAmount(m and 0b10000 > 0, s[4]).build())
    }

    private val currentSkills = hashMapOf<UUID, IntArray>()
    private val currentSkillsModified = hashMapOf<UUID, Int>()

    override fun onClick(player: Player, slot: Int, clickType: ClickType, result: InventoryConditionResult) {
        result.isCancel = true

        val add = when (clickType) {
            ClickType.LEFT_CLICK -> 1
            ClickType.RIGHT_CLICK -> 5
            else -> 0
        }
        when (slot) {
            11 -> inv.setItemStack(11, strBookItem.upgradeSkill(player, 0, add).build())
            12 -> inv.setItemStack(12, dexBookItem.upgradeSkill(player, 1, add).build())
            13 -> inv.setItemStack(13, intBookItem.upgradeSkill(player, 2, add).build())
            14 -> inv.setItemStack(14, defBookItem.upgradeSkill(player, 3, add).build())
            15 -> inv.setItemStack(15, agiBookItem.upgradeSkill(player, 4, add).build())

            4 -> {
                player.playSound(resetSound)
                player.resetSkillAssigns()
                refreshSkillBooks(player)
            }
        }

        inv.title = getTitle(player)
    }

    override fun onClose(player: Player) {
        //println("Close")
        checkPlayerItems(player, currentSkills.remove(player.uuid)!!)
        currentSkillsModified.remove(player.uuid)
    }

    private fun ItemStackBuilder.upgradeSkill(player: Player, skillIndex: Int, add: Int) = apply {
        val cs = currentSkills[player.uuid] ?: return@apply
        val rem = player.remainingSkillAssigns
        val to100 = 100 - player.assignedSkill(skillIndex)
        //player.sendMessage(to100.toString())
        val actualAdd = if (rem > 0 && to100 > 0) {
            player.playSound(successAddSound)
            add.coerceAtMost(rem).coerceAtMost(to100)
        } else {
            player.playSound(failAddSound)
            0
        }
        cs[skillIndex] = (cs[skillIndex] + actualAdd).also { this.skillBookAmount(currentSkillsModified[player.uuid]!! and (1 shl skillIndex) > 0, it) }
        player.upgradeSkill(skillIndex, actualAdd)
        //player.sendMessage(String.format("%x", player.assignedSkills))
    }

    // TODO: localized skill book items
    private val eachPointInThisSkill = Component.text("Each point in this skill", NamedTextColor.GRAY)
    private val damageYouMayInflict = Component.text("damage you may inflict.", NamedTextColor.GRAY)
    private val strBookItem = abilityBookItem(Component.text("Strength", NamedTextColor.DARK_GREEN), listOf(
        eachPointInThisSkill,
        LegacyComponentSerializer.legacy('&').deserialize("&7will&d increase &7any damage"),
        LegacyComponentSerializer.legacy('&').deserialize("&7you deal and increase the &2✤ Earth"),
        damageYouMayInflict
    ))
    private val dexBookItem = abilityBookItem(Component.text("Dexterity", NamedTextColor.YELLOW), listOf(
        eachPointInThisSkill,
        LegacyComponentSerializer.legacy('&').deserialize("&7will&d increase &7the chance"),
        LegacyComponentSerializer.legacy('&').deserialize("&7to do a critical hit (doubling"),
        LegacyComponentSerializer.legacy('&').deserialize("&7damage) and increase the &e✦ Thunder"),
        damageYouMayInflict
    ))
    private val intBookItem = abilityBookItem(Component.text("Intelligence", NamedTextColor.AQUA), listOf(
        eachPointInThisSkill,
        LegacyComponentSerializer.legacy('&').deserialize("&7will&d reduce &7mana used"),
        LegacyComponentSerializer.legacy('&').deserialize("&7by spells and increase the &b❉ Water"),
        damageYouMayInflict
    ))
    private val defBookItem = abilityBookItem(Component.text("Defense", NamedTextColor.RED), listOf(
        eachPointInThisSkill,
        LegacyComponentSerializer.legacy('&').deserialize("&7will&d reduce &7any damage"),
        LegacyComponentSerializer.legacy('&').deserialize("&7you take and increase the &c✹ Fire"),
        damageYouMayInflict
    ))
    private val agiBookItem = abilityBookItem(Component.text("Agility", NamedTextColor.WHITE), listOf(
        eachPointInThisSkill,
        LegacyComponentSerializer.legacy('&').deserialize("&7will&d increase &7the chance"),
        LegacyComponentSerializer.legacy('&').deserialize("&7to dodge attacks and increase"),
        LegacyComponentSerializer.legacy('&').deserialize("&7the &f❋ Air &7damage you may"),
        Component.text("inflict", NamedTextColor.GRAY)
    ))
}

private val successAddSound = Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.MASTER, 1f, 1.1f)
private val failAddSound = Sound.sound(SoundEvent.BLOCK_ANVIL_PLACE, Sound.Source.MASTER, 1f, 1.1f)
private val resetSound = Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1f, 1f)

private fun ItemStackBuilder.skillBookAmount(modified: Boolean, amount: Int) = apply {
    if (amount == 0) amount(1)
    else if (amount < 0) amount(-amount)
    else if (amount >= 64) amount(64)
    else amount(amount)
    skillBookLore(this, modified, amount)
}

private val resetSkillPointsItem = ItemStack.builder(Material.GOLDEN_SHOVEL)
    .displayNameNonItalic(Component.translatable("gui.menu.reset_skill_points", NamedTextColor.YELLOW))
    .loreNonItalic(Component.text()
        .append(Component.translatable("gui.menu.reset_skill_points.cost", NamedTextColor.GRAY))
        .append(Component.translatable("gui.menu.reset_skill_points.cost.soul_points", NamedTextColor.WHITE))
        .build())
    .meta<ItemMetaBuilder> { it
        .damage(21)
        .unbreakable(true)
        .hideAllFlags()
    }
    .build()

private val tomesItem = ItemStack.builder(Material.ENCHANTED_BOOK)
    .displayNameNonItalic(Component.translatable("gui.menu.tomes", Style.style(NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)))
    .build()

private fun guildBannerItem() = ItemStack.builder(Material.BLACK_BANNER)
    .displayNameNonItalic(Component.translatable("gui.menu.guild", Style.style(NamedTextColor.AQUA, TextDecoration.BOLD)))
    .build()

private val settingsItem = ItemStack.builder(Material.CRAFTING_TABLE)
    .displayNameNonItalic(Component.translatable("gui.menu.settings"))
    .build()

private val jukeboxItem = ItemStack.builder(Material.JUKEBOX)
    .displayNameNonItalic(Component.translatable("gui.menu.jukebox", NamedTextColor.AQUA))
    .build()


private fun abilityBookItem(name: Component, lore: List<Component>) = ItemStack.builder(Material.BOOK).amount(1)
    .displayNameNonItalic(Component.translatable("gui.menu.skill_book.upgrade", NamedTextColor.LIGHT_PURPLE, name))
    .loreNonItalic(skillBookLoreGeneric.apply { addAll(lore) })

private fun skillBookLore(builder: ItemStackBuilder, modified: Boolean, amount: Int) {
    builder.loreNonItalic(builder.lore.let { val m = it.toMutableList(); setSkillBookLore(m, amount, modified); m })
}

/*
&r       &7&lNow           &6&lNext
&r       &a%.1f%%     §a>§2>§a>§2>    &e%.1f%%
&r     &7%d points          §6%d points
 */
private val skillBookLoreGeneric get() = mutableListOf<Component>(
    Component.empty(),
    Component.text()
        .append(Component.translatable("gui.menu.skill_book.now.spaces"))
        .append(Component.translatable("gui.menu.skill_book.now", NamedTextColor.GRAY, TextDecoration.BOLD))
        .append(Component.translatable("gui.menu.skill_book.next.spaces"))
        .append(Component.translatable("gui.menu.skill_book.next", NamedTextColor.GOLD, TextDecoration.BOLD))
        .build(),
    Component.empty(),
    Component.empty(),
    Component.empty(),
)

private fun setSkillBookLore(lore: MutableList<Component>, value: Int, modified: Boolean) {
    lore[2] = Component.text()
        .append(Component.text("       ${(skillPercentage(value) * 1000).roundToInt() / 10f}%     >", NamedTextColor.GREEN))
        .append(Component.text(">", NamedTextColor.DARK_GREEN))
        .append(Component.text(">", NamedTextColor.GREEN))
        .append(Component.text(">    ", NamedTextColor.DARK_GREEN))
        .append(Component.text("${(skillPercentage(value + 1) * 1000).roundToInt() / 10f}%", NamedTextColor.YELLOW))
        .build()
    lore[3] = Component.text()
        .append(Component.translatable("gui.menu.skill_book.now.points", NamedTextColor.GRAY, Component.text(value)))
        .append(Component.translatable("gui.menu.skill_book.next.points", NamedTextColor.GOLD, Component.text(value + 1)))
        .build()
    if (lore.size > 10 && !modified) {
        lore.removeAt(lore.size - 1)
        lore.removeAt(lore.size - 1)
    } else if (lore.size <= 10 && modified) {
        lore.add(Component.empty())
        lore.add(Component.translatable("gui.menu.skill_book.modified", COLOR_WYNN))
    }
}