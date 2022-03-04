package com.wynnlab.minestom.particle.minestom

import com.wynnlab.minestom.particle.adventure.Particle
import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap
import it.unimi.dsi.fastutil.objects.Object2ShortMap
import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.Point
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.utils.binary.BinaryWriter

fun <D : Particle.Data, E : Particle.ExtraData?> createParticlePacket(particle: Particle<D, E>, emitter: Particle.Emitter): ParticlePacket {
    error("Emitter is not Position")
}

fun <D : Particle.Data, E : Particle.ExtraData?> createParticlePacket(particle: Particle<D, E>, emitter: Point): ParticlePacket {
    val (offX, offY, offZ, extra) = particle.particleData()
    return ParticlePacket(
        ids.getShort(particle.name()).toInt(),
        particle.longDistance(),
        emitter.x(),
        emitter.y(),
        emitter.z(),
        offX,
        offY,
        offZ,
        extra,
        particle.count(),
        when (val e = particle.extraData()) {
            is ParticleTypes.BinaryData -> {
                val writer = BinaryWriter()
                e.accept(writer)
                writer.toByteArray()
            }
            else -> ByteArray(0)
        }
    )
}


val ids: Object2ShortMap<Key> = Object2ShortArrayMap<Key>(net.minestom.server.particle.Particle.values().size).apply {
    for (v in net.minestom.server.particle.Particle.values()) {
        put(v.namespace(), v.id().toShort())
    }
}