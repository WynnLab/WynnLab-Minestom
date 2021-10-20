@file:Suppress("Unused", "OverrideOnly")

package com.wynnlab.minestom.particle.minestom

import com.wynnlab.minestom.particle.adventure.Particle
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.minestom.server.adventure.audience.PacketGroupingAudience
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

fun <D : Particle.Data, E : Particle.ExtraData?> Audience.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    when (this) {
        is PacketGroupingAudience -> showParticle(particle, emitter)
        is ForwardingAudience.Single -> showParticle(particle, emitter)
        is ForwardingAudience -> showParticle(particle, emitter)
        is Player -> showParticle(particle, emitter)
    }
}

fun <D : Particle.Data, E : Particle.ExtraData?> Audience.showParticle(particle: Particle<D, E>, emitter: Point) {
    when (this) {
        is PacketGroupingAudience -> showParticle(particle, emitter)
        is ForwardingAudience.Single -> showParticle(particle, emitter)
        is ForwardingAudience -> showParticle(particle, emitter)
        is Player -> showParticle(particle, emitter)
    }
}

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    audiences().forEach { it.showParticle(particle, emitter) }
}

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.showParticle(particle: Particle<D, E>, emitter: Point) {
    audiences().forEach { it.showParticle(particle, emitter) }
}

fun <D : Particle.Data, E : Particle.ExtraData?> PacketGroupingAudience.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    sendGroupedPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> PacketGroupingAudience.showParticle(particle: Particle<D, E>, emitter: Point) {
    sendGroupedPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> Player.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    playerConnection.sendPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> Player.showParticle(particle: Particle<D, E>, emitter: Point) {
    playerConnection.sendPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.Single.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    audience().showParticle(particle, emitter)
}

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.Single.showParticle(particle: Particle<D, E>, emitter: Point) {
    audience().showParticle(particle, emitter)
}