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
        meta.isSmall = true
        meta.isHasNoBasePlate = true
        meta.isHasNoGravity = true
        meta.isInvisible = true
        meta.customName = text
        meta.isCustomNameVisible = true
        meta.setNotifyAboutChanges(true)
    }
}