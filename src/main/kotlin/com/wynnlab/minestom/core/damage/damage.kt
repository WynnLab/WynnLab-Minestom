package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.core.player.refreshActionBar
import com.wynnlab.minestom.entities.CustomEntity
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Player.attack(other: CustomEntity, modifiers: DamageModifiers) {
    val target = CustomEntity.DamageTarget(other)
    val damage = Damage(1, 0, 0, 0, 0, 0)
    val finalDamage = damageModified(DamageSource.Player(this), target, damage, modifiers)
    if (!finalDamage.zero) {
        damageIndicators(this, target, finalDamage)

        // TODO: poison, exploding, ls, ms, thorns, reflection
    }
    other.takeKnockback(.4f, sin(position.yaw() * (PI / 180f)), -cos(position.yaw() * (PI / 180f)))
}

fun Player.attack(other: Player, modifiers: DamageModifiers) { //TODO: merge
    val target = DamageTarget.Player(other)
    val damage = Damage(1, 0, 0, 0, 0, 0)
    val finalDamage = damageModified(DamageSource.Player(this), target, damage, modifiers)
    if (!finalDamage.zero) {
        damageIndicators(this, target, finalDamage)
    }
    other.takeKnockback(.4f, sin(position.yaw() * (PI / 180f)), -cos(position.yaw() * (PI / 180f)))
}

// returns final damage
fun damageModified(source: DamageSource, target: DamageTarget, damage: Damage, modifiers: DamageModifiers): Damage {
    // TODO: defense
    val finalDamage = damage.applyConversion(modifiers.conversion)
    damageRaw(target, finalDamage.sum)
    return finalDamage
}

fun damageRaw(target: DamageTarget, amount: Int) {
    target.damage(amount.toFloat())
    //if (target.isDead && target !is Player) target.remove()
    if (target is Player) refreshActionBar(target)
}

val playerMaxHealthTag: Tag<Int> = Tag.Integer("max-health").defaultValue(20)