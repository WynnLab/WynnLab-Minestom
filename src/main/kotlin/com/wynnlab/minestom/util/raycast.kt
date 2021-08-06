package com.wynnlab.minestom.util

import net.minestom.server.entity.Entity
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.Position
import net.minestom.server.utils.Vector

fun Entity.rayCastBlock(direction: Vector = position.direction, maxDistance: Double): BlockPosition? =
    rayCastBlock(direction, maxDistance) { !it.isAir }

fun Entity.rayCastBlock(direction: Vector = position.direction, maxDistance: Double, condition: (Block) -> Boolean): BlockPosition? =
    rayCast(direction, maxDistance) {
        if (condition((instance ?: return@rayCast null).getBlock(it.toBlockPosition()))) it.toBlockPosition() else null
    }

fun Entity.rayCastEntity(direction: Vector = position.direction, maxDistance: Double): Entity? =
    rayCastEntity(direction, maxDistance) { true }

fun Entity.rayCastEntity(direction: Vector = position.direction, maxDistance: Double, condition: (Entity) -> Boolean): Entity? =
    rayCast(direction, maxDistance) {
        val instance = instance ?: return@rayCast null
        for (e in instance.getChunkEntities(instance.getChunkAt(it))) {
            if (e == this) continue
            if (e.boundingBox.intersect(it) && condition(e)) return@rayCast e
        }
        null
    }

fun <T> Entity.rayCast(direction: Vector, maxDistance: Double, condition: (Position) -> T?): T? {
    val pos = Position()
    pos.set(position)
    pos.add(.0, eyeHeight, .0)

    val multiplier = if (maxDistance < 25) maxDistance / 25.0 else 1.0

    val x = direction.x * multiplier
    val y = direction.y * multiplier
    val z = direction.z * multiplier

    val max = maxDistance * maxDistance

    while (position.getDistanceSquared(pos) < max) {
        /*ParticleCreator.createParticlePacket(Particle.CLOUD, pos.x, pos.y, pos.z, 0f, 0f, 0f, 1).let {
            if (this is Player) playerConnection.sendPacket(it)
        }*/
        condition(pos)?.let { return it }
        pos.add(x, y, z)
    }

    return null
}