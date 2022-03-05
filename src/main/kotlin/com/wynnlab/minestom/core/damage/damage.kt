package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.core.player.refreshActionBar
import com.wynnlab.minestom.entities.CustomEntity
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Player.attack(target: CustomEntity, modifiers: DamageModifiers) = DamageSource.Player(this).attack(CustomEntity.DamageTarget(target), modifiers)

fun Player.attack(target: Player, modifiers: DamageModifiers) = DamageSource.Player(this).attack(DamageTarget.Player(target), modifiers)

fun DamageSource.attack(target: DamageTarget, modifiers: DamageModifiers) {
    val finalDamage = calculateDamage(this, target, modifiers)
    val rawDamage = finalDamage.sum
    if (rawDamage > 0) {
        damageRaw(target, rawDamage)
        damageIndicators(this, target, finalDamage)
        if (!modifiers.spell) {
            poison(this, target)
            /*exploding(this, target)
            lifeSteal(this, target)
            manaSteal(this, target)*/
        }
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