package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.core.player.skillPercentage
import com.wynnlab.minestom.items.Identification
import com.wynnlab.minestom.items.itemDamageTag

val standardConversion = doubleArrayOf(1.0, .0, .0, .0, .0, .0)
private val noDamage = doubleArrayOf(.0, .0, .0, .0, .0, .0)

// Melee Neutral = (Base Dam) * (1 + (IDs) - (Def)) + (Raw Melee)
// Melee Elemental = (Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))
// Spell Neutral = (Base Dam) * (1 + (IDs) - (Def)) * (Att Speed) * (Spell Base Multiplier) + (Raw Spell) * (Spell Base Multiplier)
// Spell Elemental = [(Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))] * (Att Speed) * (Spell Base Multiplier)

fun calculateDamage(source: DamageSource, target: DamageTarget, modifiers: DamageModifiers): Damage {
    val pvp = source is DamageSource.Player && target is DamageTarget.Player
    val baseDamage = getBaseDamage(source, modifiers.conversion) ?: return Damage.Zero
    val damageModifiers = getDamageModifiers(source, modifiers.spell, pvp)
    return getModifiedDamage(source, target, baseDamage, damageModifiers, modifiers.multiplier, modifiers.spell)
}

private fun getBaseDamage(source: DamageSource, conversion: Conversion): Damage? {
    val itemBaseDamage = (source.weapon ?: return null).getTag(itemDamageTag) ?: return null
    val damage = Damage { i ->
        (itemBaseDamage[i * 2 + 1] - itemBaseDamage[i * 2]).let { if (it > 0) com.wynnlab.minestom.random.nextInt(it) else it } + itemBaseDamage[i * 2]
    }
    val neutral = damage.neutral
    return damage.with { i, d ->
        if (i > 0) (neutral * conversion[i] + d).toInt() else (neutral * conversion.neutral).toInt()
    }
}

private fun getDamageModifiers(source: DamageSource, spell: Boolean, pvp: Boolean): FloatArray {
    val strength = skillPercentage(source.getSkill(0)).let { if (pvp) it.coerceAtMost(.6f) else it }
    val dexterity = if (com.wynnlab.minestom.random.nextDouble() < skillPercentage(source.getSkill(1)).let {
        if (pvp) it.coerceAtMost(.35f) else it
    }) 1 else 0

    return FloatArray(6) { i ->
        var value = strength + dexterity
        value += (if (spell) source.getId(Identification.SpellDamage) else source.getId(Identification.DamageBonus)) / 100f
        if (i > 0) value += (source.getId(when (i) {
            1 -> Identification.BonusEarthDamage
            2 -> Identification.BonusThunderDamage
            3 -> Identification.BonusWaterDamage
            4 -> Identification.BonusFireDamage
            5 -> Identification.BonusAirDamage
            else -> error("unreachable")
        })) / 100f
        value
    }
}

private fun getModifiedDamage(source: DamageSource, target: DamageTarget, baseDamage: Damage, modifiers: FloatArray, multiplier: Float, spell: Boolean): Damage {
    return Damage { i ->
        (if (spell) {
            val attackSpeedMultiplier = source.attackSpeedMultiplier
            if (i == 0) (baseDamage.neutral * (1 + modifiers[0] - target.baseDefense) * attackSpeedMultiplier + source.getId(Identification.SpellDamageRaw)) * multiplier
            else (baseDamage[i] * (1 + modifiers[i]) - target.defense[i - 1] * (1 + target.getEleDefPercent(i - 1))) * attackSpeedMultiplier * multiplier
        } else {
            if (i == 0) baseDamage.neutral * (1 + modifiers[0] - target.baseDefense) + source.getId(Identification.DamageBonusRaw)
            else baseDamage[i] * (1 + modifiers[i]) - target.defense[i - 1] * (1 + target.getEleDefPercent(i - 1))
        }).toInt()
    }
}
