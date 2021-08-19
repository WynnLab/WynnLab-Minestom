package com.wynnlab.minestom.mob

import com.wynnlab.minestom.entities.CustomEntity
import com.wynnlab.minestom.items.AttackSpeed
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.EntityType
import net.minestom.server.item.ItemStack
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagWritable

fun writeMobMeta(writer: TagWritable, builder: MobBuilder) {
    writer.setTag(mobTypeIdTag, builder.type.id())

    writer.setTag(mobSoundTypesTag, intArrayOf(
        builder.hurtSound?.sound?.id() ?: -1,
        builder.deathSound?.sound?.id() ?: -1,
        builder.ambientSound?.sound?.id() ?: -1,
    ))
    writer.setTag(mobSoundPitchesTag, intArrayOf(
        builder.hurtSound?.pitch?.toBits() ?: 0,
        builder.deathSound?.pitch?.toBits() ?: 0,
        builder.ambientSound?.pitch?.toBits() ?: 0,
    ))

    writer.setTag(mobNameTag, builder.name)
    writer.setTag(mobLevelTag, builder.level.toShort())
    writer.setTag(mobMaxHealthTag, builder.maxHealth)
    writer.setTag(mobAttackSpeedTag, builder.attackSpeed.ordinal.toByte())
    writer.setTag(mobBaseDefenseTag, builder.baseDefense)

    writer.setTag(mobDamageTag, intArrayOf(
        builder.damage.neutral.first, builder.damage.neutral.last,
        builder.damage.earth.first, builder.damage.earth.last,
        builder.damage.thunder.first, builder.damage.thunder.last,
        builder.damage.water.first, builder.damage.water.last,
        builder.damage.fire.first, builder.damage.fire.last,
        builder.damage.air.first, builder.damage.air.last,
    ))
    writer.setTag(mobDefenseTag, intArrayOf(
        builder.elementalDefense.earth,
        builder.elementalDefense.thunder,
        builder.elementalDefense.water,
        builder.elementalDefense.fire,
        builder.elementalDefense.air,
    ))

    writer.setTag(mobGlowingTag, builder.glowing)
    writer.setTag(mobBurningTag, builder.burning)
}

fun getCustomMob(item: ItemStack): CustomEntity? = try {
    val soundTypes = item.getTag(mobSoundTypesTag)!!
    val soundPitches = item.getTag(mobSoundPitchesTag)!!
    val mob = object : CustomEntity.Default(EntityType.fromId(item.getTag(mobTypeIdTag)!!)!!) {
        override val hurtSound = soundTypes[0].takeIf { it != -1 }?.let { Sound.sound(SoundEvent.fromId(it)!!, Sound.Source.HOSTILE, 1f, Float.fromBits(soundPitches[0])) }
            ?: super.hurtSound
        override val deathSound = soundTypes[1].takeIf { it != -1 }?.let { Sound.sound(SoundEvent.fromId(it)!!, Sound.Source.HOSTILE, 1f, Float.fromBits(soundPitches[0])) }
            ?: super.deathSound
        override val ambientSound = soundTypes[2].takeIf { it != -1 }?.let { Sound.sound(SoundEvent.fromId(it)!!, Sound.Source.HOSTILE, .2f, Float.fromBits(soundPitches[0])) }
            ?: super.ambientSound
    }

    mob.setName(item.getTag(mobNameTag)!!)
    mob.level = item.getTag(mobLevelTag)!!.toInt()
    val maxHealth = item.getTag(mobMaxHealthTag)!!
    mob.maxHealth = maxHealth.toFloat()
    mob.health = maxHealth.toFloat()
    mob.attackSpeed = AttackSpeed.values()[item.getTag(mobAttackSpeedTag)!!.toInt()]
    mob.baseDefense = item.getTag(mobBaseDefenseTag)!!

    val damage = item.getTag(mobDamageTag)!!
    mob.damage.neutral = damage[0]..damage[1]
    mob.damage.earth = damage[2]..damage[3]
    mob.damage.thunder = damage[4]..damage[5]
    mob.damage.water = damage[6]..damage[7]
    mob.damage.fire = damage[8]..damage[9]
    mob.damage.air = damage[10]..damage[11]

    val defense = item.getTag(mobDefenseTag)!!
    mob.defense.earth = defense[0]
    mob.defense.thunder = defense[1]
    mob.defense.water = defense[2]
    mob.defense.fire = defense[3]
    mob.defense.air = defense[4]

    mob.isGlowing = item.getTag(mobGlowingTag)!!
    mob.isOnFire = item.getTag(mobBurningTag)!!

    mob
} catch (_: Exception) {
    println("Something went wrong deserializing a mob")
    null
}

val mobTypeIdTag = Tag.Integer("mob-type-id")
val mobSoundTypesTag = Tag.IntArray("mob-sound-types")
val mobSoundPitchesTag = Tag.IntArray("mob-sound-pitches")

val mobNameTag = Tag.String("mob-name")
val mobLevelTag = Tag.Short("mob-level")
val mobMaxHealthTag = Tag.Integer("mob-max-health")
val mobAttackSpeedTag = Tag.Byte("mob-attack-speed")
val mobBaseDefenseTag = Tag.Float("mob-base-defense")

val mobDamageTag = Tag.IntArray("mob-damage")
val mobDefenseTag = Tag.IntArray("mob-defense")

val mobGlowingTag = Tag.Byte("mob-glowing").map({ it != 0.toByte() }, { if (it) 1.toByte() else 0.toByte() }).defaultValue(false)
val mobBurningTag = Tag.Byte("mob-glowing").map({ it != 0.toByte() }, { if (it) 1.toByte() else 0.toByte() }).defaultValue(false)