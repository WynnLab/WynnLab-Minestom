package com.wynnlab.minestom.particle.minestom

import com.wynnlab.minestom.particle.adventure.Particle
import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap
import it.unimi.dsi.fastutil.objects.Object2ShortMap
import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.Pos
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.utils.binary.BinaryWriter

fun <D : Particle.Data, E : Particle.ExtraData?> createParticlePacket(particle: Particle<D, E>, emitter: Particle.Emitter): ParticlePacket {
    TODO("Emitter is not Position")
}

fun <D : Particle.Data, E : Particle.ExtraData?> createParticlePacket(particle: Particle<D, E>, emitter: Pos): ParticlePacket {
    val packet = ParticlePacket()
    packet.particleId = ids.getShort(particle.name).toInt()
    packet.longDistance = particle.longDistance

    packet.x = emitter.x()
    packet.y = emitter.y()
    packet.z = emitter.z()

    val (offX, offY, offZ, extra) = particle.particleData
    packet.offsetX = offX
    packet.offsetY = offY
    packet.offsetZ = offZ

    packet.particleData = extra
    packet.particleCount = particle.count

    packet.data = when (val e = particle.extraData) {
        is ParticleType.BinaryData -> {
            val writer = BinaryWriter()
            e.accept(writer)
            writer.toByteArray()
        }
        else -> ByteArray(0)
    }

    return packet
}


val ids: Object2ShortMap<Key> = Object2ShortArrayMap<Key>(net.minestom.server.particle.Particle.values().size).apply {
    for (v in net.minestom.server.particle.Particle.values()) {
        put(v.namespace(), v.id().toShort())
    }
}