/*package com.wynnlab.minestom.util

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.instance.block.Block

fun Entity.rayCastBlock(direction: Vec = position.direction(), maxDistance: Double): Point? =
    rayCastBlock(direction, maxDistance) { !it.isAir }

fun Entity.rayCastBlock(direction: Vec = position.direction(), maxDistance: Double, condition: (Block) -> Boolean): Point? =
    rayCast(direction, maxDistance) {
        if (condition((instance ?: return@rayCast null).getBlock(it))) it else null
    }

fun Entity.rayCastEntity(direction: Vec = position.direction(), maxDistance: Double): Entity? =
    rayCastEntity(direction, maxDistance) { true }

fun Entity.rayCastEntity(direction: Vec = position.direction(), maxDistance: Double, condition: (Entity) -> Boolean): Entity? =
    rayCast(direction, maxDistance) {
        val instance = instance ?: return@rayCast null
        for (e in instance.getChunkEntities(instance.getChunkAt(it))) {
            if (e == this) continue
            if (e.boundingBox.intersect(it) && condition(e)) return@rayCast e
        }
        null
    }

//TODO
fun <T> Entity.rayCast(direction: Vec, maxDistance: Double, condition: (Pos) -> T?): T? {
    var pos = position.add(.0, eyeHeight, .0)

    val multiplier = if (maxDistance < 25) maxDistance / 25.0 else 1.0

    val x = direction.x() * multiplier
    val y = direction.y() * multiplier
    val z = direction.z() * multiplier

    val max = maxDistance * maxDistance

    while (position.distanceSquared(pos) < max) {
        /*ParticleCreator.createParticlePacket(Particle.CLOUD, pos.x, pos.y, pos.z, 0f, 0f, 0f, 1).let {
            if (this is Player) playerConnection.sendPacket(it)
        }*/
        condition(pos)?.let { return it }
        pos = pos.add(x, y, z)
    }

    return null
}*/