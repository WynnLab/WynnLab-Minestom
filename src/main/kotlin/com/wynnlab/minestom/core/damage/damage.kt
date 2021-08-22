package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.core.player.refreshActionBar
import com.wynnlab.minestom.entities.CustomEntity
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Player.attack(target: CustomEntity, modifiers: DamageModifiers) = attack(CustomEntity.DamageTarget(target), modifiers)

fun Player.attack(target: Player, modifiers: DamageModifiers) = attack(DamageTarget.Player(target), modifiers)

fun Player.attack(target: DamageTarget, modifiers: DamageModifiers) {
    val finalDamage = calculateDamage(DamageSource.Player(this), target, modifiers)
    if (!finalDamage.zero) {
        damageIndicators(this, target, finalDamage)
        damageRaw(target, finalDamage.sum)
        // TODO: poison, exploding, ls, ms, thorns, reflection
    }
    target.takeKnockback(.4f, sin(position.yaw() * (PI / 180f)), -cos(position.yaw() * (PI / 180f)))
}

fun damageRaw(target: DamageTarget, amount: Int) {
    target.damage(amount.toFloat())
    //if (target.isDead && target !is Player) target.remove()
    if (target is Player) refreshActionBar(target)
}

val playerMaxHealthTag: Tag<Int> = Tag.Integer("max-health").defaultValue(20)