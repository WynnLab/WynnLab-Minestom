package com.wynnlab.minestom.particle.adventure;

import com.wynnlab.minestom.particle.adventure.impl.ParticleImpl;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface Particle<D extends Particle.Data, E extends Particle.ExtraData> extends Examinable {
    static <D extends Data, E extends ExtraData> Particle<D, E> particle(Key name, int count, D data, E extraData) {
        return particle(name, count, data, extraData, false);
    }

    static <D extends Data, E extends ExtraData> Particle<D, E> particle(Key name, int count, D data, E extraData, boolean longDistance) {
        return new ParticleImpl<D, E>(count, data, extraData, longDistance) {
            @Override
            public Key name() {
                return name;
            }
        };
    }

    static <D extends Data, E extends ExtraData> Particle<D, E> particle(Type<D, E> type, int count, D data, E extraData) {
        return particle(type, count, data, extraData, false);
    }

    static <D extends Data, E extends ExtraData> Particle<D, E> particle(Type<D, E> type, int count, D data, E extraData, boolean longDistance) {
        return particle(type.key(), count, data, extraData, longDistance);
    }

    static <D extends Data, E extends ExtraData> Particle<D, E> particle(Type<D, E> type, int count, D data) {
        return particle(type, count, data, false);
    }

    static <D extends Data, E extends ExtraData> Particle<D, E> particle(Type<D, E> type, int count, D data, boolean longDistance) {
        return particle(type.key(), count, data, null, longDistance);
    }
    /*
        fun <D : Data, E : ExtraData?> particle(type: () -> Type<D, E>, count: Int, data: D, extraData: E, longDistance: Boolean = false): Particle<D, E> =
        object : ParticleImpl<D, E>(count, data, extraData, longDistance) {
        override val name get() = type().key()
        }

        fun <D : Data> particle(name: Key, count: Int, data: D, longDistance: Boolean = false): Particle<D, Nothing?> =
        object : ParticleImpl<D, Nothing?>(count, data, null, longDistance) {
        override val name = name
        }

        fun <D : Data> particle(type: () -> Type<D, Nothing?>, count: Int, data: D, longDistance: Boolean = false): Particle<D, Nothing?> =
        object : ParticleImpl<D, Nothing?>(count, data, null, longDistance) {
        override val name get() = type().key()
     */

    Key name();
    int count();
    D particleData();
    E extraData();
    boolean longDistance();

    interface Type<D extends Data, E extends ExtraData> extends Keyed {}

    interface Data extends Examinable {
        float component1();
        float component2();
        float component3();
        float component4();
    }

    interface ExtraData extends Examinable {}

    interface Emitter {}
}
