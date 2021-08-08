package com.wynnlab.minestom.particle.adventure.impl

import com.wynnlab.minestom.particle.adventure.Particle
import net.kyori.examination.ExaminableProperty
import net.kyori.examination.string.StringExaminer
import java.util.stream.Stream

internal abstract class ParticleImpl<D : Particle.Data, E : Particle.ExtraData?>(
    override val count: Int,
    override val particleData: D,
    override val extraData: E,
    override val longDistance: Boolean
) : Particle<D, E> {
    override fun examinableProperties(): Stream<out ExaminableProperty> = Stream.of(
        ExaminableProperty.of("name", name),
        ExaminableProperty.of("count", count),
        ExaminableProperty.of("particleData", particleData),
        ExaminableProperty.of("extraData", extraData),
        ExaminableProperty.of("longDistance", longDistance)
    )

    override fun toString(): String = examine(StringExaminer.simpleEscaping())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParticleImpl<*, *>

        if (count != other.count) return false
        if (particleData != other.particleData) return false
        if (extraData != other.extraData) return false
        if (longDistance != other.longDistance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = count
        result = 31 * result + particleData.hashCode()
        result = 31 * result + (extraData?.hashCode() ?: 0)
        result = 31 * result + longDistance.hashCode()
        return result
    }
}