package com.wynnlab.minestom.core.damage

import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.tag.Tag
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Entity.attack(other: LivingEntity, modifiers: DamageModifiers) {
    val damage = Damage(1, 0, 0, 0, 0, 0)
    val finalDamage = damageModified(this, other, damage, modifiers)
    if (!finalDamage.zero) {
        if (this is Player) damageIndicators(this, other, finalDamage)

        // TODO: poison, exploding, ls, ms, thorns, reflection
    }
    other.takeKnockback(.4f, sin(position.yaw * (PI / 180f)), -cos(position.yaw * (PI / 180f)))
}

// returns final damage
fun damageModified(source: Entity, entity: LivingEntity, damage: Damage, modifiers: DamageModifiers): Damage {
    // TODO: defense
    val finalDamage = damage.applyConversion(modifiers.conversion)
    damageRaw(source, entity, finalDamage.sum)
    return finalDamage
}

fun damageRaw(source: Entity, entity: LivingEntity, amount: Int) {
    entity.damage(DamageType.fromEntity(source), (amount * 20f) / entity.getTag(maxHealthTag)!!.toFloat())
    if (entity.isDead && entity !is Player) entity.remove()
}

val maxHealthTag = Tag.Integer("max-health").defaultValue(20)