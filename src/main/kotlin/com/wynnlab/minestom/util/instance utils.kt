package com.wynnlab.minestom.util

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.EntityTracker
import net.minestom.server.instance.Instance
import net.minestom.server.utils.chunk.ChunkUtils

fun <R : Any> Instance.forNearbyEntities(point: Point, radius: Double, mapNotNull: (Entity) -> R?, action: (R) -> Unit) {
    var chunkRange = (radius / Chunk.CHUNK_SECTION_SIZE).toInt()
    if (point.x() % 16 != 0.0 || point.z() % 16 != 0.0) {
        chunkRange++ // Need to loop through surrounding chunks to properly support borders
    }
    // Loop through range
    if (radius % 16 == 0.0) {
        // Fast path for exact chunk range
        ChunkUtils.forChunksInRange(point, chunkRange) { chunkX: Int, chunkZ: Int ->
            for (entity in entityTracker.chunkEntities(chunkX, chunkZ, EntityTracker.Target.ENTITIES)) {
                val e = mapNotNull(entity) ?: continue
                action(e)
            }
        }
    } else {
        // Slow path for non-exact chunk range
        val squaredRange = radius * radius
        ChunkUtils.forChunksInRange(point, chunkRange) { chunkX: Int, chunkZ: Int ->
            val chunkEntities = entityTracker.chunkEntities(chunkX, chunkZ, EntityTracker.Target.ENTITIES) as List<Entity>
            if (chunkEntities.isEmpty()) return@forChunksInRange
            chunkEntities.forEach { entity ->
                val position: Point = entity.position
                if (point.distanceSquared(position) <= squaredRange) {
                    val e = mapNotNull(entity)
                    if (e != null) action(e)
                }
            }
        }
    }
}