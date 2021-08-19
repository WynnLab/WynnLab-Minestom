package com.wynnlab.minestom.items

import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagWritable

/*fun readItemMeta(reader: TagReadable): ItemBuilderMeta? = try {
    val meta = when (reader.getTag(typeTag)) {
        1.toByte() -> ItemBuilderMeta.Damage()
        2.toByte() -> ItemBuilderMeta.Defense()
        else -> error("Invalid type")
    }

    meta.sockets = reader.getTag(socketsTag)!!.toInt()

    val skillRequirements = reader.getTag(skillRequirementsTag)!!
    meta.skillRequirements.strength = skillRequirements[0].toUByte().toInt()
    meta.skillRequirements.dexterity = skillRequirements[1].toUByte().toInt()
    meta.skillRequirements.intelligence = skillRequirements[2].toUByte().toInt()
    meta.skillRequirements.defense = skillRequirements[3].toUByte().toInt()
    meta.skillRequirements.agility = skillRequirements[4].toUByte().toInt()

    if (meta is ItemBuilderMeta.Damage) {
        meta.attackSpeed = AttackSpeed.values()[reader.getTag(attackSpeedTag)!!.toInt()]

        val damage = reader.getTag(damageTag)!!
        meta.damage.earth = damage[0]..damage[1]
        meta.damage.thunder = damage[2]..damage[3]
        meta.damage.water = damage[4]..damage[5]
        meta.damage.fire = damage[6]..damage[7]
        meta.damage.air = damage[8]..damage[9]
    }

    meta
} catch (e: Exception) {
    null
}*/


fun writeItemMeta(writer: TagWritable, builder: ItemBuilder) {
    writer.setTag(itemTypeIdTag, (builder.type.ordinal.let { if (it < 5) 1 else it - 3 }).toByte())

    writer.setTag(itemSocketsTag, builder.sockets.toByte())

    writer.setTag(skillRequirementsTag, byteArrayOf(
        builder.skillRequirements.strength.toUByte().toByte(),
        builder.skillRequirements.dexterity.toUByte().toByte(),
        builder.skillRequirements.intelligence.toUByte().toByte(),
        builder.skillRequirements.defense.toUByte().toByte(),
        builder.skillRequirements.agility.toUByte().toByte(),
    ))

    if (builder is ItemBuilder.Weapon) {
        writer.setTag(itemAttackSpeedTag, builder.attackSpeed.ordinal.toByte())
        writer.setTag(itemDamageTag, intArrayOf(
            builder.damage.earth.first, builder.damage.earth.last,
            builder.damage.thunder.first, builder.damage.thunder.last,
            builder.damage.water.first, builder.damage.water.last,
            builder.damage.fire.first, builder.damage.fire.last,
            builder.damage.air.first, builder.damage.air.last,
        ))
    } else if (builder is ItemBuilder.Defense) {
        writer.setTag(itemHealthTag, builder.health.toUShort().toShort())
        writer.setTag(itemDefenseTag, byteArrayOf(
            builder.defense.earth.toUByte().toByte(),
            builder.defense.thunder.toUByte().toByte(),
            builder.defense.water.toUByte().toByte(),
            builder.defense.fire.toUByte().toByte(),
            builder.defense.air.toUByte().toByte(),
        ))
    }

    for ((k, v) in builder.ids) {
        writer.setTag(k.tag, v)
    }
}

val itemTypeIdTag = Tag.Byte("item-type-id")

val itemSocketsTag: Tag<Byte> = Tag.Byte("item-sockets").defaultValue(0)
val skillRequirementsTag = Tag.ByteArray("item-skill-requirements")

val itemAttackSpeedTag = Tag.Byte("item-attack-speed")
val itemDamageTag = Tag.IntArray("item-damage")

val itemHealthTag = Tag.Short("item-health")
val itemDefenseTag = Tag.ByteArray("item-defense")