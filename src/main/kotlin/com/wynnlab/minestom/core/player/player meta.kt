package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.items.*
import net.minestom.server.entity.Player
import net.minestom.server.tag.TagReadable

fun getId(player: Player, id: Identification): Int {
    var i = 0
    player.equipment.forEach { if (it != null) i += id.get(it) }
    return i
}

fun getHealth(player: Player): Int {
    var i = 0
    player.defenseEquipment.forEach { if (it != null) i += it.getTag(itemHealthTag)!! }
    return i
}

fun getAttackSpeed(player: Player): AttackSpeed? {
    val eq = player.equipment
    var i = (eq[0] ?: return null).getTag(itemAttackSpeedTag)!!.toInt()
    eq.forEach { if (it != null) i += Identification.AttackSpeed.get(it) }
    return AttackSpeed.values()[i.coerceIn(0, 6)]
}

fun getDamageArray(player: Player) = player.itemInMainHand.takeIf { it.getTag(itemTypeIdTag) == 1.toByte() }
    ?.getTag(itemDamageTag)

fun getDefense(player: Player): Defense {
    val d = Defense(0, 0, 0, 0, 0)
    for (item in player.defenseEquipment) {
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

val Player.equipment get() = listOf(
    itemWeapon,
    itemHelmet,
    itemChestplate,
    itemLeggings,
    itemBoots,
    itemRing1,
    itemRing2,
    itemBracelet,
    itemNecklace,
)

val Player.defenseEquipment get() = listOf(
    itemHelmet,
    itemChestplate,
    itemLeggings,
    itemBoots,
    itemRing1,
    itemRing2,
    itemBracelet,
    itemNecklace,
)

inline val Player.itemWeapon get() = itemInMainHand.takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.WEAPON }
inline val Player.itemHelmet get() = helmet.takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.HELMET }
inline val Player.itemChestplate get() = chestplate.takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.CHESTPLATE }
inline val Player.itemLeggings get() = leggings.takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.LEGGINGS }
inline val Player.itemBoots get() = boots.takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.BOOTS }
inline val Player.itemRing1 get() = inventory.getItemStack(9).takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.RING }
inline val Player.itemRing2 get() = inventory.getItemStack(10).takeIf { it.getTag(itemTypeIdTag) ==ItemTypeId.RING }
inline val Player.itemBracelet get() = inventory.getItemStack(11).takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.BRACELET }
inline val Player.itemNecklace get() = inventory.getItemStack(12).takeIf { it.getTag(itemTypeIdTag) == ItemTypeId.NECKLACE }