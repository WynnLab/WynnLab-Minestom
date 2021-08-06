package com.wynnlab.minestom.core.damage

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

data class Damage(
    val neutral: Int,
    val earth: Int,
    val thunder: Int,
    val water: Int,
    val fire: Int,
    val air: Int,
) : Iterable<DamagePart> {
    val sum = neutral + earth + thunder + water + fire + air
    val zero = sum == 0

    fun applyConversion(conversion: Conversion) = Damage(
        (neutral * conversion.neutral).toInt(),
        (earth * conversion.earth).toInt(),
        (thunder * conversion.thunder).toInt(),
        (water * conversion.water).toInt(),
        (fire * conversion.fire).toInt(),
        (air * conversion.air).toInt(),
    )

    override fun iterator() = Iterator()

    inner class Iterator : kotlin.collections.Iterator<DamagePart> {
        private var i = 0

        override fun hasNext() = i < 6

        override fun next(): DamagePart {
            ++i
            return DamagePart(DamageType.values()[i - 1], when (i - 1) {
                0 -> neutral
                1 -> earth
                2 -> thunder
                3 -> water
                4 -> fire
                else -> air
            })
        }
    }
}

data class DamagePart(
    val type: DamageType,
    val value: Int
)

enum class DamageType(val color: TextColor, val icon: Char) {
    Neutral(NamedTextColor.DARK_RED, '❤'),
    Earth(NamedTextColor.DARK_GREEN, 'e'),
    Thunder(NamedTextColor.YELLOW, 't'),
    Water(NamedTextColor.AQUA, 'w'),
    Fire(NamedTextColor.RED, 'f'),
    Air(NamedTextColor.WHITE, 'a')
}

data class DamageModifiers(
    val spell: Boolean,
    val multiplier: Float,
    val conversion: Conversion
)

data class Conversion(
    val neutral: Float,
    val earth: Float,
    val thunder: Float,
    val water: Float,
    val fire: Float,
    val air: Float,
)

val NeutralConversion = Conversion(1f, 0f, 0f, 0f, 0f, 0f)

val NeutralDamageModifiers = DamageModifiers(false, 1f, NeutralConversion)