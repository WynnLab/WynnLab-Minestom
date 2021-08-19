package com.wynnlab.minestom.items

import com.wynnlab.minestom.core.Element
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
) : Iterable<Damage.Part> {
    fun set(other: Damage) {
        neutral = other.neutral
        earth = other.earth
        thunder = other.thunder
        water = other.water
        fire = other.fire
        air = other.air
    }

    override fun iterator() = object : Iterator<Part> {
        private var i = 0

        override fun hasNext() = i < 6

        override fun next(): Part = when (i++) {
            0 -> Part(Element.Neutral, neutral)
            1 -> Part(Element.Earth, earth)
            2 -> Part(Element.Thunder, thunder)
            3 -> Part(Element.Water, water)
            4 -> Part(Element.Fire, fire)
            5 -> Part(Element.Air, air)
            else -> throw NoSuchElementException()
        }
    }

    data class Part(
        val element: Element,
        val value: IntRange
    )
}

data class Defense(
    @JvmField var earth: Int,
    @JvmField var thunder: Int,
    @JvmField var water: Int,
    @JvmField var fire: Int,
    @JvmField var air: Int,
) : Iterable<Defense.Part> {
    fun set(other: Defense) {
        earth = other.earth
        thunder = other.thunder
        water = other.water
        fire = other.fire
        air = other.air
    }

    override fun iterator() = object : Iterator<Part> {
        private var i = 0

        override fun hasNext() = i < 5

        override fun next(): Part = when (i++) {
            0 -> Part(Element.Earth, earth)
            1 -> Part(Element.Thunder, thunder)
            2 -> Part(Element.Water, water)
            3 -> Part(Element.Fire, fire)
            4 -> Part(Element.Air, air)
            else -> throw NoSuchElementException()
        }
    }

    data class Part(
        val element: Element,
        val value: Int
    )
}

data class SkillRequirements(
    @JvmField var strength: Int,
    @JvmField var dexterity: Int,
    @JvmField var intelligence: Int,
    @JvmField var defense: Int,
    @JvmField var agility: Int
)