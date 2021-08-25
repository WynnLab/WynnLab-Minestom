package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.items.Identification
import com.wynnlab.minestom.particle.adventure.Particle
import com.wynnlab.minestom.particle.minestom.EFFECT
import com.wynnlab.minestom.particle.minestom.ENTITY_EFFECT
import com.wynnlab.minestom.particle.minestom.ParticleType
import com.wynnlab.minestom.particle.minestom.showParticle
import net.minestom.server.MinecraftServer
import net.minestom.server.tag.Tag
import net.minestom.server.utils.time.TimeUnit

fun poison(source: DamageSource, target: DamageTarget) {
    val poison = (source.getId(Identification.Poison) / 3f).toInt()
    if (poison <= 0) return
    if (!target.hasTag(poisonedTag)) {
        target.setTag(poisonedTag, 3)
        val task = MinecraftServer.getSchedulerManager().buildTask {
            poisonTask(target, poison)
            val p = target.getTag(poisonedTag)!!
            if (p <= 0) target.removeTag(poisonedTag)
            else target.setTag(poisonedTag, (p - 1).toByte())
        }.repeat(1, TimeUnit.SECOND).schedule()
        MinecraftServer.getSchedulerManager().buildTask {
            if (!target.hasTag(poisonedTag)) task.cancel()
        }.delay(4, TimeUnit.SECOND).schedule()
    }
}

private val poisonedTag = Tag.Byte("poisoned").defaultValue(0)

private fun poisonTask(target: DamageTarget, amountPerSecond: Int) {
    target.viewersAsAudience.showParticle(poisonParticle, target.position)
    damageRaw(target, amountPerSecond)
}

private val poisonParticle = Particle.particle(EFFECT, 15, ParticleType.Color(0f, 1f, 0f, 1f))

fun exploding(source: DamageSource, target: DamageTarget) {

}

fun lifeSteal(source: DamageSource, target: DamageTarget) {

}

fun manaSteal(source: DamageSource, target: DamageTarget) {

}