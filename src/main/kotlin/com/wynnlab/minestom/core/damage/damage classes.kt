package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.core.Element
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import java.util.*

interface DamageSource {
    @JvmInline
    value class Player(val player: net.minestom.server.entity.Player) : DamageSource
}

interface DamageTarget : TagHandler {
    fun damage(value: Float)
    val health: Float
    val maxHealth: Float
    val instance: Instance?
    val position: Pos
    val eyeHeight: Double
    val uuid: UUID
    val isDead: Boolean
    val customName: Component?

    @JvmInline
    value class Player(val player: net.minestom.server.entity.Player) : DamageTarget {
        override fun damage(value: Float) {
            player.damage(net.minestom.server.entity.damage.DamageType("minecraft:custom"),
                value * 20f / maxHealth)
        }

        override val instance get() = player.instance
        override val position get() = player.position
        override val eyeHeight get() = player.eyeHeight
        override val uuid get() = player.uuid
        override val isDead get() = player.isDead
        override val customName get() = player.name
        override val health get() = player.health * maxHealth / 20f
        override val maxHealth get() = player.getTag(playerMaxHealthTag)!!.toFloat()

        override fun <T : Any?> getTag(tag: Tag<T>): T? = player.getTag(tag)

        override fun <T : Any?> setTag(tag: Tag<T>, value: T?) = player.setTag(tag, value)
    }
}

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
            return DamagePart(Element.values()[i - 1], when (i - 1) {
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
    val element: Element,
    val value: Int
)

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