package com.wynnlab.minestom.entities

import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.EntityAnimationPacket
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import java.util.concurrent.CompletableFuture

abstract class CustomEntity(entityType: EntityType) : Entity(entityType) {
    var health = 0f
    var maxHealth = 0f
    inline val isDead get() = health <= 0f

    open val hurtSound: Sound = Sound.sound(SoundEvent.ENTITY_GENERIC_HURT, Sound.Source.AMBIENT, 1f, 1f)
    open val deathSound: Sound = Sound.sound(SoundEvent.ENTITY_GENERIC_DEATH, Sound.Source.AMBIENT, 1f, 1f)
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

        velocity = Vec.ZERO

        EventDispatcher.call(EntityDeathEvent(this))

        remove()
    }

    val belowNameHologram = Hologram(null)

    override fun setInstance(instance: Instance, spawnPosition: Pos): CompletableFuture<Void>? {
        return super.setInstance(instance, spawnPosition).thenRun {
            belowNameHologram.setInstance(instance, hologramPosition())
        }
    }

    /*override fun sendPositionUpdate(clientSide: Boolean) {
        super.sendPositionUpdate(clientSide)
        belowNameHologram.refreshPosition(hologramPosition())
    }*/
    /*override fun refreshPosition(newPosition: Pos) {
        super.refreshPosition(newPosition)
        belowNameHologram.refreshPosition(hologramPosition())
    }*/

    override fun update(time: Long) {
        if (belowNameHologram.isCustomNameVisible)
            belowNameHologram.refreshPosition(hologramPosition())
    }

    override fun remove() {
        super.remove()
        belowNameHologram.remove()
    }

    private fun hologramPosition() = position.add(.0, eyeHeight, .0)

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

        override fun <T : Any?> getTag(tag: Tag<T>): T? = ce.getTag(tag)

        override fun <T : Any?> setTag(tag: Tag<T>, value: T?) = ce.setTag(tag, value)
    }

    class Default(entityType: EntityType) : CustomEntity(entityType)
}

private val healthTag = Tag.Integer("health")