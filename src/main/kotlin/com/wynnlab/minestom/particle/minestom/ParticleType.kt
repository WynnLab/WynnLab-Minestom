package com.wynnlab.minestom.particle.minestom

import com.wynnlab.minestom.particle.adventure.Particle
import com.wynnlab.minestom.particle.adventure.impl.ParticleImpl
import com.wynnlab.minestom.particle.minestom.extra.*
import net.kyori.adventure.key.Keyed
import net.kyori.examination.ExaminableProperty
import net.minestom.server.utils.binary.BinaryWriter
import java.util.function.Consumer
import java.util.stream.Stream
import net.minestom.server.particle.Particle as MinestomParticle

sealed class ParticleType <D : Particle.Data, E : Particle.ExtraData?>
constructor(private val minestomParticle: MinestomParticle) :
    Particle.Type<D, E>, Keyed by minestomParticle {

    internal class DefaultParticleType(minestomParticle: MinestomParticle) :
        ParticleType<OffsetAndSpeed, Nothing?>(minestomParticle)

    internal class ColorParticleType(minestomParticle: MinestomParticle) :
        ParticleType<Color, Nothing?>(minestomParticle)

    data class OffsetAndSpeed(val offX: Float, val offY: Float, val offZ: Float, val speed: Float) : Particle.Data {
        override fun examinableProperties(): Stream<out ExaminableProperty> = Stream.of(
            ExaminableProperty.of("offX", offX),
            ExaminableProperty.of("offY", offY),
            ExaminableProperty.of("offZ", offZ),
            ExaminableProperty.of("speed", speed),
        )
    }

    data class Color(val red: Float, val green: Float, val blue: Float, val alpha: Float) : Particle.Data {
        override fun examinableProperties(): Stream<out ExaminableProperty> = Stream.of(
            ExaminableProperty.of("red", red),
            ExaminableProperty.of("green", green),
            ExaminableProperty.of("blue", blue),
            ExaminableProperty.of("alpha", alpha),
        )
    }

    fun interface BinaryData : Particle.ExtraData, Consumer<BinaryWriter>

    // Internal stuff
    internal class Block(minestomParticle: MinestomParticle) : ParticleType<OffsetAndSpeed, BlockState>(minestomParticle)
    internal object DUST : ParticleType<OffsetAndSpeed, Dust>(MinestomParticle.DUST)
    internal object DUST_COLOR_TRANSITION : ParticleType<OffsetAndSpeed, DustTransition>(MinestomParticle.DUST_COLOR_TRANSITION)
    internal object ITEM : ParticleType<OffsetAndSpeed, Item>(MinestomParticle.ITEM)
    internal object VIBRATION : ParticleType<OffsetAndSpeed, Vibration>(MinestomParticle.VIBRATION)
}