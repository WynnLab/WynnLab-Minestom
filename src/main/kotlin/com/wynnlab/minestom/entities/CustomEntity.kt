package com.wynnlab.minestom.entities

import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.EntityAnimationPacket
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Position

abstract class CustomEntity(entityType: EntityType) : Entity(entityType) {
    var health = 0f
    var maxHealth = 0f
    inline val isDead get() = health <= 0f

    open val hurtSound: Sound = Sound.sound(SoundEvent.GENERIC_HURT, Sound.Source.AMBIENT, 1f, 1f)
    open val deathSound: Sound = Sound.sound(SoundEvent.GENERIC_DEATH, Sound.Source.AMBIENT, 1f, 1f)
    open val ambientSound: Sound? = null

    fun damage(value: Float) {
        health -= value

        if (isDead) {
            kill()
            return
        }

        viewersAsAudience.playSound(hurtSound)

        val entityAnimationPacket = EntityAnimationPacket()
        entityAnimationPacket.entityId = entityId
        entityAnimationPacket.animation = EntityAnimationPacket.Animation.TAKE_DAMAGE
        sendPacketToViewers(entityAnimationPacket)
    }

    fun kill() {
        viewersAsAudience.playSound(deathSound)

        triggerStatus(3)

        velocity.zero()

        EventDispatcher.call(EntityDeathEvent(this))

        remove()
    }

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

    @JvmInline
    value class DamageTarget(val ce: CustomEntity) : com.wynnlab.minestom.core.damage.DamageTarget {
        override fun damage(value: Float) = ce.damage(value)

        override val health get() = ce.health
        override val maxHealth get() = ce.maxHealth
        override val instance get() = ce.getInstance()
        override val position get() = ce.getPosition()
        override val eyeHeight get() = ce.eyeHeight
        override val uuid get() = ce.getUuid()
        override val isDead get() = ce.isDead
        override val customName get() = ce.customName
    }

    class Default(entityType: EntityType) : CustomEntity(entityType)
}

private val healthTag = Tag.Integer("health")