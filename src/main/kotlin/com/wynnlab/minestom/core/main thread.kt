package com.wynnlab.minestom.core

import com.wynnlab.minestom.core.damage.playerMaxHealthTag
import com.wynnlab.minestom.core.player.getHealth
import com.wynnlab.minestom.core.player.getId
import com.wynnlab.minestom.items.Identification
import net.minestom.server.MinecraftServer
import net.minestom.server.attribute.Attribute
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.utils.time.TimeUnit

fun runIdTasks() {
    val manager = MinecraftServer.getSchedulerManager()
    manager.buildTask(::everyTick).repeat(1L, TimeUnit.SERVER_TICK).schedule()
    manager.buildTask(::everySecond).repeat(1L, TimeUnit.SECOND).schedule()
    manager.buildTask(::every4Seconds).repeat(4L, TimeUnit.SECOND).schedule()
}

fun everyTick() {
    for (player in MinecraftServer.getConnectionManager().onlinePlayers) {
        val healthBefore = player.getTag(playerMaxHealthTag)!!
        val maxHealth = (505 + getHealth(player) + getId(player, Identification.HealthBonus)).coerceAtLeast(1)
        if (maxHealth != healthBefore) {
            player.setTag(playerMaxHealthTag, maxHealth)
            player.health = player.health * (healthBefore / maxHealth.toFloat())
        }

        player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = .1f + getId(player, Identification.WalkSpeed) * .001f
    }
}

fun everySecond() {
    for (player in MinecraftServer.getConnectionManager().onlinePlayers) {
        player.food = (player.food + 1).coerceIn(0, 20)

        val jumpHeight = getId(player, Identification.JumpHeight)
        if (jumpHeight != 0) {
            player.addEffect(Potion(PotionEffect.JUMP_BOOST, (jumpHeight - 1).toByte(), 21, 0b100))
        } else player.removeEffect(PotionEffect.JUMP_BOOST)
    }
}

fun every4Seconds() {
    for (player in MinecraftServer.getConnectionManager().onlinePlayers) {
        val regenRaw = getId(player, Identification.HealthRegenRaw)
        if (regenRaw == 0) continue
        val maxHealth = player.getTag(playerMaxHealthTag)!!
        val regen = regenRaw * (1 + getId(player, Identification.HealthRegen) / 100f) * if (regenRaw < 0) -1 else 1
        player.health = (player.health + regen * 20f / maxHealth.toFloat()).coerceIn(0.000001f, 20f)
    }
}

/*fun every10Seconds() {

}*/