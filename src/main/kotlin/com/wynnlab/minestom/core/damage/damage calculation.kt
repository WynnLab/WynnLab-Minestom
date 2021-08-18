package com.wynnlab.minestom.core.damage

import net.minestom.server.entity.Player

val standardConversion = doubleArrayOf(1.0, .0, .0, .0, .0, .0)
private val noDamage = doubleArrayOf(.0, .0, .0, .0, .0, .0)

/*fun getDamage(source: DamageSource, target: DamageTarget, melee: Boolean, multiplier: Double = 1.0, conversion: DoubleArray = standardConversion): DoubleArray {
    val pvp = false //hasScoreboardTag("pvp")

    // Melee Neutral = (Base Dam) * (1 + (IDs) - (Def)) + (Raw Melee)
    // Melee Elemental = (Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))
    // Spell Neutral = (Base Dam) * (1 + (IDs) - (Def)) * (Att Speed) * (Spell Base Multiplier) + (Raw Spell) * (Spell Base Multiplier)
    // Spell Elemental = [(Base Dam) * (1 + (IDs)) - ((Ele Def) * (1 + (Ele Def %)))] * (Att Speed) * (Spell Base Multiplier)

    val baseDamage = source.getBaseDamage(conversion)
    val modifiers = source.getDamageModifiers(melee, pvp)

    return source.getModifiedDamage(baseDamage, modifiers, multiplier, melee)
}

private fun DamageSource.getBaseDamage(conversion: DoubleArray): DoubleArray {
    val damageRanges = if (hasWeaponInHand() ?: return noDamage)
        inventory.itemInMainHand.itemMeta.data.getIntArray("damage") ?: return noDamage
    else return noDamage

    val damages = DoubleArray(6) { i ->
        (damageRanges[i * 2 + 1] - damageRanges[i * 2].let { if (it > 0) random.nextInt(it) else it } + damageRanges[i * 2]).toDouble()
    }

    repeat(6) { i ->
        damages[i] = if (i > 0) damages[0] * conversion[i] + damages[i] else damages[0] * conversion[0]
    }

    return damages
}

private fun DamageSource.getDamageModifiers(melee: Boolean, pvp: Boolean): DoubleArray {
    val strength = skillPercentage(getSkill(0)).let { if (pvp) it.coerceAtMost(.6) else it }
    val dexterity = if (random.nextDouble() < skillPercentage(getSkill(1)).let { if (pvp) it.coerceAtMost(.35) else it }) 1.0 else .0

    return DoubleArray(6) { i ->
        var value = strength + dexterity
        value += if (melee)
            getId("damage_bonus") / 100.0
        else
            getId("spell_damage") / 100.0
        if (i > 0)
            value += getId("bonus_${elementNamesLC[i - 1]}_damage") / 100.0
        value
    }
}

private fun DamageSource.getModifiedDamage(baseDamage: DoubleArray, modifiers: DoubleArray, multiplier: Double, melee: Boolean): DoubleArray {
    val result = noDamage

    if (melee) {
        result[0] = baseDamage[0] * (1 + modifiers[0] /*- def*/) + getId("damage_bonus_raw")
        repeat(5) { i ->
            result[i + 1] = baseDamage[i + 1] * (1 + modifiers[i + 1]) /*- def*/
        }
    } else {
        val attackSpeedSpellMultiplier = weaponAttackSpeed!!.spellMultiplier
        result[0] = (baseDamage[0] * (1 + modifiers[0] /*- def*/) * attackSpeedSpellMultiplier + getId("spell_damage_raw")) * multiplier
        repeat(5) { i ->
            result[i + 1] = (baseDamage[i + 1] * (1 + modifiers[i + 1]) /*- def*/) * attackSpeedSpellMultiplier * multiplier
        }
    }

    return result
}*/