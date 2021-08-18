package com.wynnlab.minestom.mob

import net.minestom.server.entity.EntityType
import net.minestom.server.item.Material
import net.minestom.server.sound.SoundEvent

data class Sound(
    var sound: SoundEvent,
    var pitch: Float,
)

fun typeItemOf(type: EntityType) = when (type) {
    EntityType.AREA_EFFECT_CLOUD -> Material.DRAGON_BREATH
    EntityType.BOAT -> Material.OAK_BOAT
    EntityType.DRAGON_FIREBALL -> Material.DRAGON_HEAD
    EntityType.ENDER_DRAGON -> Material.DRAGON_EGG
    EntityType.EVOKER_FANGS -> Material.TOTEM_OF_UNDYING
    EntityType.EXPERIENCE_ORB -> Material.GLASS_BOTTLE
    EntityType.EYE_OF_ENDER -> Material.ENDER_EYE
    EntityType.FALLING_BLOCK -> Material.SAND
    EntityType.FIREWORK_ROCKET -> Material.FIREWORK_ROCKET
    EntityType.GIANT -> Material.ZOMBIE_HEAD
    EntityType.GLOW_ITEM_FRAME -> Material.GLOW_ITEM_FRAME
    EntityType.ILLUSIONER -> Material.GLASS_PANE
    EntityType.IRON_GOLEM -> Material.IRON_BLOCK
    EntityType.ITEM -> Material.DIAMOND
    EntityType.FIREBALL -> Material.FIRE_CHARGE
    EntityType.LEASH_KNOT -> Material.LEAD
    EntityType.LIGHTNING_BOLT -> Material.LIGHTNING_ROD
    EntityType.LLAMA_SPIT -> Material.PURPLE_CARPET
    EntityType.MARKER -> Material.BARRIER
    EntityType.SPAWNER_MINECART -> Material.SPAWNER
    EntityType.SMALL_FIREBALL -> Material.FIREWORK_STAR
    EntityType.WITHER -> Material.NETHER_STAR
    EntityType.WITHER_SKULL -> Material.WITHER_SKELETON_SKULL
    EntityType.PLAYER -> Material.PLAYER_HEAD
    EntityType.FISHING_BOBBER -> Material.FISHING_ROD
    else -> Material.fromNamespaceId("${type.name()}_spawn_egg")
        ?: Material.fromNamespaceId(type.name())!!
}