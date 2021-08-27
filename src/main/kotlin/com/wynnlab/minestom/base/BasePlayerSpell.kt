package com.wynnlab.minestom.base

import com.wynnlab.minestom.core.damage.DamageSource
import com.wynnlab.minestom.core.damage.DamageTarget
import com.wynnlab.minestom.particle.adventure.Particle
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player

abstract class BasePlayerSpell(
    val player: Player,
    duration: Int,
    val cost: Int = 0
) : BaseSpell(duration) {
    val isCloneSpell = player.getTag(playerCloneClassTag)!!
    val damageSource = DamageSource.Player(player)

    //TODO
    fun sound(at: Point = player.position, sound: Sound) {}
    fun particle(at: Point = player.position, particle: Particle<*, *>) {}
    fun targets(center: Point, x: Double, y: Double, z: Double): Iterable<DamageTarget> = emptyList()

    inline fun clone(block: () -> Unit) {
        if (isCloneSpell) block()
    }
}