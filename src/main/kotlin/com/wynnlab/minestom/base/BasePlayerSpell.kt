package com.wynnlab.minestom.base

import com.wynnlab.minestom.core.damage.DamageSource
import com.wynnlab.minestom.core.damage.DamageTarget
import com.wynnlab.minestom.entities.CustomEntity
import com.wynnlab.minestom.particle.adventure.Particle
import com.wynnlab.minestom.particle.minestom.showParticle
import net.kyori.adventure.sound.Sound
import net.minestom.server.adventure.audience.PacketGroupingAudience
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
    fun sound(at: Point = player.position, sound: Sound) {
        player.playSound(sound, at.x(), at.y(), at.z())
        player.viewersAsAudience.playSound(sound, at.x(), at.y(), at.z())
    }
    fun particle(at: Point = player.position, particle: Particle<*, *>) {
        player.showParticle(particle, at)
        PacketGroupingAudience.of(player.viewers.filter { it.getDistanceSquared(player) <= 400 }).showParticle(particle, at)
    }
    fun targets(center: Point, radius: Double): Iterable<DamageTarget> {
        val entities = player.instance!!.getNearbyEntities(center, radius) //TODO
        return entities.mapNotNull {
            if (it.uuid == player.uuid) null
            else if (it is Player) DamageTarget.Player(it)
            else if (it is CustomEntity) CustomEntity.DamageTarget(it)
            else null
        }
    }

    inline fun clone(block: () -> Unit) {
        if (isCloneSpell) block()
    }
}