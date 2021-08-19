package com.wynnlab.minestom.entities

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.ArmorStandMeta

class Hologram(text: Component?) : Entity(EntityType.ARMOR_STAND) {
    init {
        val meta = getEntityMeta() as ArmorStandMeta

        meta.setNotifyAboutChanges(false)
        meta.isMarker = true
        meta.isHasNoBasePlate = true
        meta.isHasNoGravity = true
        meta.isInvisible = true
        if (text != null) {
            meta.customName = text
            meta.isCustomNameVisible = true
        }
        meta.setNotifyAboutChanges(true)
    }

    val hologramNameVisible = true

    override fun setCustomName(customName: Component?) {
        super.setCustomName(customName)
        isCustomNameVisible = hologramNameVisible && customName != null
    }
}