package com.wynnlab.minestom.util

import net.minestom.server.coordinate.Vec

class Vectors(private val start: Vec, private val end: Vec, private val step: Double) : Iterable<Vec> {
    private val distSqr = end.distanceSquared(start)
    private val addVec = (end - start).normalize() * step

    override fun iterator(): Iterator<Vec> = VectorIterator()

    inner class VectorIterator : Iterator<Vec> {
        private var current = start
        private var currentDist = .0
        private inline val currentDistSqr get() = currentDist * currentDist
        private var ended = false

        override fun hasNext(): Boolean = !ended

        override fun next(): Vec {
            currentDist += step
            if (currentDistSqr > distSqr) ended = true
            if (ended) return end
            current += addVec
            return current
        }
    }
}

class VectorsBuilder internal constructor(private val start: Vec, private val end: Vec) {
    infix fun step(step: Double) = Vectors(start, end, step)
}