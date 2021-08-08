package com.wynnlab.minestom.entities

import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.instance.Instance
import net.minestom.server.utils.Position

open class LivingEntityWithBelowNameTag(entityType: EntityType) : LivingEntity(entityType) {
    val belowNameHologram = Hologram(null)

    override fun setInstance(instance: Instance, spawnPosition: Position) {
        super.setInstance(instance, spawnPosition)
        belowNameHologram.setInstance(instance, hologramPosition())
    }

    override fun sendPositionUpdate(clientSide: Boolean) {
        super.sendPositionUpdate(clientSide)
        belowNameHologram.refreshPosition(hologramPosition())
    }

    override fun remove() {
        super.remove()
        belowNameHologram.remove()
    }

    private fun hologramPosition(): Position {
        val pos = Position()
        pos.set(position)
        pos.add(.0, eyeHeight, .0)
        return pos
    }
}