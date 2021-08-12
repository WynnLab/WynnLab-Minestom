package com.wynnlab.minestom.items

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class Rarity(val nameColor: TextColor) {
    Normal(NamedTextColor.WHITE),
    Set(NamedTextColor.GREEN),
    Unique(NamedTextColor.YELLOW),
    Rare(NamedTextColor.LIGHT_PURPLE),
    Legendary(NamedTextColor.AQUA),
    Fabled(NamedTextColor.RED),
    Mythic(NamedTextColor.DARK_PURPLE),
}

enum class AttackSpeed(val display: String) {
    SuperSlow("Super Slow"),
    VerySlow("Very Slow"),
    Slow("Slow"),
    Normal("Normal"),
    Fast("Fast"),
    VeryFast("Very Fast"),
    SuperFast("Super Fast")
}

data class Damage(
    @JvmField var neutral: IntRange,
    @JvmField var earth: IntRange,
    @JvmField var thunder: IntRange,
    @JvmField var water: IntRange,
    @JvmField var fire: IntRange,
    @JvmField var air: IntRange,
)

data class Defense(
    @JvmField var earth: Int,
    @JvmField var thunder: Int,
    @JvmField var water: Int,
    @JvmField var fire: Int,
    @JvmField var air: Int,
)

data class SkillRequirements(
    @JvmField var strength: Int,
    @JvmField var dexterity: Int,
    @JvmField var intelligence: Int,
    @JvmField var defense: Int,
    @JvmField var agility: Int
)