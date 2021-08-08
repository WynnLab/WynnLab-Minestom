package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleType
import net.minestom.server.utils.binary.BinaryWriter

data class Vibration(
    val startX: Float,
    val startY: Float,
    val startZ: Float,
    val endX: Float,
    val endY: Float,
    val endZ: Float,
    val ticks: Int
) : ParticleType.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeFloat(startX)
        t.writeFloat(startY)
        t.writeFloat(startZ)
        t.writeFloat(endX)
        t.writeFloat(endY)
        t.writeFloat(endZ)
        t.writeInt(ticks)
    }
}