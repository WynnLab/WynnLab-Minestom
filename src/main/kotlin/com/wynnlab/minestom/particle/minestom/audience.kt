@file:Suppress("Unused", "OverrideOnly")

package com.wynnlab.minestom.particle.minestom

import com.wynnlab.minestom.particle.adventure.Particle
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.minestom.server.adventure.audience.PacketGroupingAudience
import net.minestom.server.entity.Player
import net.minestom.server.utils.Position

fun <D : Particle.Data, E : Particle.ExtraData?> Audience.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    when (this) {
        is PacketGroupingAudience -> showParticle(particle, emitter)
        is ForwardingAudience.Single -> showParticle(particle, emitter)
        is ForwardingAudience -> showParticle(particle, emitter)
        is Player -> showParticle(particle, emitter)
    }
}

fun <D : Particle.Data, E : Particle.ExtraData?> Audience.showParticle(particle: Particle<D, E>, emitter: Position) {
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

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.showParticle(particle: Particle<D, E>, emitter: Position) {
    audiences().forEach { it.showParticle(particle, emitter) }
}

fun <D : Particle.Data, E : Particle.ExtraData?> PacketGroupingAudience.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    sendGroupedPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> PacketGroupingAudience.showParticle(particle: Particle<D, E>, emitter: Position) {
    sendGroupedPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> Player.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    playerConnection.sendPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> Player.showParticle(particle: Particle<D, E>, emitter: Position) {
    playerConnection.sendPacket(createParticlePacket(particle, emitter))
}

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.Single.showParticle(particle: Particle<D, E>, emitter: Particle.Emitter) {
    audience().showParticle(particle, emitter)
}

fun <D : Particle.Data, E : Particle.ExtraData?> ForwardingAudience.Single.showParticle(particle: Particle<D, E>, emitter: Position) {
    audience().showParticle(particle, emitter)
}