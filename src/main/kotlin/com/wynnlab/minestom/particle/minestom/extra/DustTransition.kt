package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleTypes
import net.minestom.server.utils.binary.BinaryWriter

data class DustTransition(
    val fromRed: Float,
    val fromGreen: Float,
    val fromBlue: Float,
    val toRed: Float,
    val toGreen: Float,
    val toBlue: Float,
    val scale: Float
) : ParticleTypes.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeFloat(fromRed)
        t.writeFloat(fromGreen)
        t.writeFloat(fromBlue)
        t.writeFloat(scale)
        t.writeFloat(toRed)
        t.writeFloat(toGreen)
        t.writeFloat(toBlue)
    }
}