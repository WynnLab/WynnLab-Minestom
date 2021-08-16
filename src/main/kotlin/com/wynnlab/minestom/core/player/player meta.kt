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
    getEquipment(player).forEach { if (it != null) i += it.getTag(itemHealthTag)!! }
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
    player.itemHelmet?.addTo(d)
    player.itemHelmet?.addTo(d)
    player.itemChestplate?.addTo(d)
    player.itemLeggings?.addTo(d)
    player.itemBoots?.addTo(d)
    player.itemRing1?.addTo(d)
    player.itemRing2?.addTo(d)
    player.itemBracelet?.addTo(d)
    player.itemNecklace?.addTo(d)
    return d
}

fun TagReadable.addTo(defense: Defense) {
    val d = getTag(itemDefenseTag)!!
    defense.earth += d[0]
    defense.thunder += d[1]
    defense.water += d[2]
    defense.fire += d[3]
    defense.air += d[4]
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

inline val Player.itemWeapon get() = itemInMainHand.takeIf { it.getTag(itemTypeIdTag) == 1.toByte() }
inline val Player.itemHelmet get() = helmet.takeIf { it.getTag(itemTypeIdTag) == 2.toByte() }
inline val Player.itemChestplate get() = chestplate.takeIf { it.getTag(itemTypeIdTag) == 3.toByte() }
inline val Player.itemLeggings get() = leggings.takeIf { it.getTag(itemTypeIdTag) == 4.toByte() }
inline val Player.itemBoots get() = boots.takeIf { it.getTag(itemTypeIdTag) == 5.toByte() }
inline val Player.itemRing1 get() = inventory.getItemStack(9).takeIf { it.getTag(itemTypeIdTag) == 6.toByte() }
inline val Player.itemRing2 get() = inventory.getItemStack(10).takeIf { it.getTag(itemTypeIdTag) == 6.toByte() }
inline val Player.itemBracelet get() = inventory.getItemStack(11).takeIf { it.getTag(itemTypeIdTag) == 8.toByte() }
inline val Player.itemNecklace get() = inventory.getItemStack(12).takeIf { it.getTag(itemTypeIdTag) == 7.toByte() }