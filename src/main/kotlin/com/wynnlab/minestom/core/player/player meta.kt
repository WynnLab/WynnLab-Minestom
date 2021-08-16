package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.items.*
import net.minestom.server.entity.Player
import net.minestom.server.tag.TagReadable

fun getId(player: Player, id: Identification): Int {
    var i = 0
    getEquipment(player).forEach { if (it != null) i += id.get(it) }
    return i
}

fun getHealth(player: Player): Int {
    var i = 0
    getDefenseEquipment(player).forEach { if (it != null) i += it.getTag(itemHealthTag)!! }
    return i
}

fun getAttackSpeed(player: Player): AttackSpeed? {
    val eq = getEquipment(player)
    var i = (eq[0] ?: return null).getTag(itemAttackSpeedTag)!!.toInt()
    eq.forEach { if (it != null) i += Identification.AttackSpeed.get(it) }
    return AttackSpeed.values()[i.coerceIn(0, 6)]
}

fun getDamageArray(player: Player) = player.itemInMainHand.takeIf { it.getTag(itemTypeIdTag) == 1.toByte() }
    ?.getTag(itemDamageTag)

fun getDefense(player: Player): Defense {
    val d = Defense(0, 0, 0, 0, 0)
    for (item in getDefenseEquipment(player)) {
        if (item == null) continue
        val a = item.getTag(itemDefenseTag)!!
        d.earth += a[0]
        d.thunder += a[1]
        d.water += a[2]
        d.fire += a[3]
        d.air += a[4]
    }
    return d
}

private fun getEquipment(player: Player) = listOf(
    player.itemWeapon,
    player.itemHelmet,
    player.itemChestplate,
    player.itemLeggings,
    player.itemBoots,
    player.itemRing1,
    player.itemRing2,
    player.itemBracelet,
    player.itemNecklace,
)

private fun getDefenseEquipment(player: Player) = listOf(
    player.itemHelmet,
    player.itemChestplate,
    player.itemLeggings,
    player.itemBoots,
    player.itemRing1,
    player.itemRing2,
    player.itemBracelet,
    player.itemNecklace,
)

inline val Player.itemWeapon get() = itemInMainHand.takeIf { it.getTag(itemTypeIdTag) == 1.toByte() }
inline val Player.itemHelmet get() = helmet.takeIf { it.getTag(itemTypeIdTag) == 2.toByte() }
inline val Player.itemChestplate get() = chestplate.takeIf { it.getTag(itemTypeIdTag) == 3.toByte() }
inline val Player.itemLeggings get() = leggings.takeIf { it.getTag(itemTypeIdTag) == 4.toByte() }
inline val Player.itemBoots get() = boots.takeIf { it.getTag(itemTypeIdTag) == 5.toByte() }
inline val Player.itemRing1 get() = inventory.getItemStack(9).takeIf { it.getTag(itemTypeIdTag) == 6.toByte() }
inline val Player.itemRing2 get() = inventory.getItemStack(10).takeIf { it.getTag(itemTypeIdTag) == 6.toByte() }
inline val Player.itemBracelet get() = inventory.getItemStack(11).takeIf { it.getTag(itemTypeIdTag) == 8.toByte() }
inline val Player.itemNecklace get() = inventory.getItemStack(12).takeIf { it.getTag(itemTypeIdTag) == 7.toByte() }