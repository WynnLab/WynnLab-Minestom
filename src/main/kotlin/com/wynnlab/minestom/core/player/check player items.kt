package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.items.skillRequirementsPositionsTag
import com.wynnlab.minestom.items.skillRequirementsTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.ItemEntity
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.builder
import net.minestom.server.item.lore

fun checkPlayerItems(player: Player, modifiedSkillPoints: IntArray = player.modifiedSkills) {
    for (i in 0 until player.inventory.size) {
        var item = player.inventory.getItemStack(i)
        var remove = false
        item = try {
            checkPlayerItem(item, modifiedSkillPoints) ?: continue
        } catch (e: SkillReqNotMet) {
            remove = true
            e.item
        }
        player.inventory.setItemStack(i, if (remove) ItemStack.AIR else item)
        if (remove) {
            if (!player.inventory.addItemStack(item))//TODO: throw
                //ItemEntity(item).setInstance(player.instance ?: return, player.position)
                player.dropItem(item)
        }
    }
}

@Throws(SkillReqNotMet::class)
fun checkPlayerItem(item: ItemStack, modifiedSkillPoints: IntArray): ItemStack? {
    //println("Checking item")
    val skillReqPos = item.getTag(skillRequirementsPositionsTag) ?: return null
    var metAll = true
    val skillRequirements = item.getTag(skillRequirementsTag)!!
    val builder = item.builder()
    val lore = ArrayList(builder.lore)
    for (s in 0..4) {
        val p = skillReqPos[s].toInt()
        if (p < 0) continue
        val req = skillRequirements[s].toInt()
        val met = modifiedSkillPoints[s] >= req
        if (!met && metAll) metAll = false
        lore[p] = skillRequirementComponent(met, req, when (s) {
            0 -> "Strength"
            1 -> "Dexterity"
            2 -> "Intelligence"
            3 -> "Defense"
            4 -> "Agility"
            else -> error("unreachable")
        })
    }
    val new = builder.lore(lore).build()
    if (!metAll) throw SkillReqNotMet(new)
    return new
}

fun checkPlayerItemSafe(item: ItemStack, modifiedSkillPoints: IntArray) = try {
    checkPlayerItem(item, modifiedSkillPoints)
} catch (e: SkillReqNotMet) {
    e.item
}

val greenCheck = Component.text("✔", NamedTextColor.GREEN/*COLOR_GREEN.textColor*/)
val redCross = Component.text("✖", NamedTextColor.RED/*COLOR_RED.textColor*/)

fun skillRequirementComponent(met: Boolean, req: Int, name: String) =
    Component.text().append(if (met) greenCheck else redCross).append(Component.text(" $name Min: $req", NamedTextColor.GRAY))
        .style { it.decoration(TextDecoration.ITALIC, false) }.build()

class SkillReqNotMet(val item: ItemStack) : Throwable()
/*private val itemsToCheck = Int2ObjectOpenHashMap<KProperty1<Player, ItemStack?>>().apply {
    put(9, Player::itemRing1)
    put(10, Player::itemRing2)
    put(11, Player::itemBracelet)
    put(12, Player::itemNecklace)
    put(PlayerInventoryUtils.HELMET_SLOT, Player::itemHelmet)
    put(PlayerInventoryUtils.CHESTPLATE_SLOT, Player::itemChestplate)
    put(PlayerInventoryUtils.LEGGINGS_SLOT, Player::itemLeggings)
    put(PlayerInventoryUtils.BOOTS_SLOT, Player::itemBoots)
}*/