@file:Suppress("Unused", "MemberVisibilityCanBePrivate")

package com.wynnlab.minestom.particle.adventure

import com.wynnlab.minestom.particle.adventure.impl.ParticleImpl
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.examination.Examinable
import org.jetbrains.annotations.ApiStatus.NonExtendable

@NonExtendable
interface Particle<D : Particle.Data, E : Particle.ExtraData?> : Examinable {
    companion object {
        fun <D : Data, E : ExtraData?> particle(name: Key, count: Int, data: D, extraData: E, longDistance: Boolean = false): Particle<D, E> =
            object : ParticleImpl<D, E>(count, data, extraData, longDistance) {
                override val name = name
            }

        fun <D : Data, E : ExtraData?> particle(type: Type<D, E>, count: Int, data: D, extraData: E, longDistance: Boolean = false): Particle<D, E> =
            particle(type.key(), count, data, extraData, longDistance)

        fun <D : Data, E : ExtraData?> particle(type: () -> Type<D, E>, count: Int, data: D, extraData: E, longDistance: Boolean = false): Particle<D, E> =
            object : ParticleImpl<D, E>(count, data, extraData, longDistance) {
                override val name get() = type().key()
            }

        fun <D : Data> particle(name: Key, count: Int, data: D, longDistance: Boolean = false): Particle<D, Nothing?> =
            object : ParticleImpl<D, Nothing?>(count, data, null, longDistance) {
                override val name = name
            }

        fun <D : Data> particle(type: Type<D, Nothing?>, count: Int, data: D, longDistance: Boolean = false): Particle<D, Nothing?> =
            particle(type.key(), count, data, null, longDistance)

        fun <D : Data> particle(type: () -> Type<D, Nothing?>, count: Int, data: D, longDistance: Boolean = false): Particle<D, Nothing?> =
            object : ParticleImpl<D, Nothing?>(count, data, null, longDistance) {
                override val name get() = type().key()
            }
    }

    val name: Key

    val count: Int

    val particleData: D

    val extraData: E

    val longDistance: Boolean

    interface Type<D : Data, E : ExtraData?> : Keyed

    interface Data : Examinable {
        operator fun component1(): Float
        operator fun component2(): Float
        operator fun component3(): Float
        operator fun component4(): Float
    }

    interface ExtraData : Examinable

    interface Emitter
}