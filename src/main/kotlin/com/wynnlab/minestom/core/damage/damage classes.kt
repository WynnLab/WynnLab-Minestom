package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.core.Element
import com.wynnlab.minestom.core.player.*
import com.wynnlab.minestom.items.Defense
import com.wynnlab.minestom.items.Identification
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.BooleanTag
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import java.util.*

interface DamageSource {
    val weapon: ItemStack?
    fun getSkill(index: Int): Int
    fun getId(identification: Identification): Int
    val attackSpeedMultiplier: Float
    val position: Pos

    @JvmInline
    value class Player(val player: net.minestom.server.entity.Player) : DamageSource {
        override val weapon get() = player.itemWeapon

        override fun getSkill(index: Int): Int = player.getEffectiveSkill(index)

        override fun getId(identification: Identification): Int = getId(player, identification)

        override val attackSpeedMultiplier: Float get() = getAttackSpeed(player)?.spellMultiplier ?: 0f

        override val position: Pos get() = player.position
    }
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
    val boundingBox: BoundingBox

    val baseDefense: Float
    val defense: Defense
    fun getEleDefPercent(index: Int): Float
    fun takeKnockback(a: Float, b: Double, c: Double)
    val viewersAsAudience: Audience

    val center get() = position.add(0.0, eyeHeight / 2, 0.0)

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
        override val boundingBox get() = player.boundingBox

        override val health get() = player.health * maxHealth / 20f
        override val maxHealth get() = player.getTag(playerMaxHealthTag)!!.toFloat()

        override val baseDefense get() = 0f //TODO
        override val defense get() = getDefense(player)

        override fun getEleDefPercent(index: Int): Float = getId(player, when (index) {
            0 -> Identification.BonusEarthDefense
            1 -> Identification.BonusThunderDefense
            2 -> Identification.BonusWaterDefense
            3 -> Identification.BonusFireDefense
            4 -> Identification.BonusAirDefense
            else -> error("unreachable")
        }) / 100f

        override fun takeKnockback(a: Float, b: Double, c: Double) = player.takeKnockback(a, b, c)

        override val viewersAsAudience get() = player.viewersAsAudience

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
            return DamagePart(Element.values()[if (i == 1) 0 else i], when (i - 1) {
                0 -> neutral
                1 -> earth
                2 -> thunder
                3 -> water
                4 -> fire
                else -> air
            })
        }
    }

    fun with(transform: (index: Int, damage: Int) -> Int) = Damage(
        transform(0, neutral),
        transform(1, earth),
        transform(2, thunder),
        transform(3, water),
        transform(4, fire),
        transform(5, air),
    )

    operator fun get(index: Int) = when (index) {
        0 -> neutral
        1 -> earth
        2 -> thunder
        3 -> water
        4 -> fire
        5 -> air
        else -> error("unreachable")
    }

    companion object {
        val Zero = Damage(0, 0, 0, 0, 0, 0)
    }
}

inline fun Damage(generator: (Int) -> Int) =
    Damage(generator(0), generator(1), generator(2), generator(3), generator(4), generator(5))

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
) {
    operator fun get(index: Int) = when (index) {
        0 -> neutral
        1 -> earth
        2 -> thunder
        3 -> water
        4 -> fire
        5 -> air
        else -> error("unreachable")
    }
}

val NeutralConversion = Conversion(1f, 0f, 0f, 0f, 0f, 0f)

val NeutralDamageModifiers = DamageModifiers(false, 1f, NeutralConversion)

private val playerPoisonedTag = BooleanTag("poisoned")