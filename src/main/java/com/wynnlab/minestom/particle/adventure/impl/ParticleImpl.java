package com.wynnlab.minestom.particle.adventure.impl;

import com.wynnlab.minestom.particle.adventure.Particle;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

@ApiStatus.Internal
public abstract class ParticleImpl<D extends Particle.Data, E extends Particle.ExtraData>/*(
        override val count: Int,
        override val particleData: D,
        override val extraData: E,
        override val longDistance: Boolean
        )*/ implements Particle<D, E> {
    private final int count;
    private final D particleData;
    private final E extraData;
    private final boolean longDistance;

    protected ParticleImpl(int count, D particleData, E extraData, boolean longDistance) {
        this.count = count;
        this.particleData = particleData;
        this.extraData = extraData;
        this.longDistance = longDistance;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("name", name()),
                ExaminableProperty.of("count", count),
                ExaminableProperty.of("particleData", particleData),
                ExaminableProperty.of("extraData", extraData),
                ExaminableProperty.of("longDistance", longDistance)
        );
    }

    @Override
    public String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticleImpl)) return false;
        ParticleImpl<?, ?> particle = (ParticleImpl<?, ?>) o;
        return name().equals(particle.name()) && count == particle.count && longDistance == particle.longDistance && particleData.equals(particle.particleData) && Objects.equals(extraData, particle.extraData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), count, particleData, extraData, longDistance);
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public D particleData() {
        return particleData;
    }

    @Override
    public E extraData() {
        return extraData;
    }

    @Override
    public boolean longDistance() {
        return longDistance;
    }
}