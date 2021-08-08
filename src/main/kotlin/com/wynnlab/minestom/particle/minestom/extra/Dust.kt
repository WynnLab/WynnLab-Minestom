package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleType
import net.minestom.server.utils.binary.BinaryWriter

data class Dust(
    val red: Float,
    val green: Float,
    val blue: Float,
    val scale: Float
) : ParticleType.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeFloat(red)
        t.writeFloat(green)
        t.writeFloat(blue)
        t.writeFloat(scale)
    }
}