package com.wynnlab.minestom.entities

import com.wynnlab.minestom.items.AttackSpeed
import com.wynnlab.minestom.items.Damage
import com.wynnlab.minestom.items.Defense
import com.wynnlab.minestom.random
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
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
    var level: Int = 0
    set(value) {
        field = value
        @Suppress("deprecation")
        setCustomName(customName)
    }
    var nameColor: TextColor = NamedTextColor.GREEN

    var health = 0f
    var maxHealth = 0f
    inline val isDead get() = health <= 0f

    var baseDefense: Float = 0f
    val defense: Defense = Defense(0, 0, 0, 0, 0)

    var attackSpeed = AttackSpeed.Normal
    val damage: Damage = Damage(0..0, 0..0, 0..0, 0..0, 0..0, 0..0)

    open val hurtSound: Sound = Sound.sound(SoundEvent.ENTITY_GENERIC_HURT, Sound.Source.AMBIENT, 1f, 1f)
    open val deathSound: Sound = Sound.sound(SoundEvent.ENTITY_GENERIC_DEATH, Sound.Source.AMBIENT, 1f, 1f)
    open val ambientSound: Sound? = null

    var isPoisoned = false

    fun setName(name: String) {
        @Suppress("deprecation")
        setCustomName(Component.text(name, nameColor))
    }

    private var customName: Component? = null
    final override fun getCustomName(): Component? {
        return customName
    }
    @Deprecated("Doesn't take nameColor into account", replaceWith = ReplaceWith("setName(name)"))
    @Suppress("unused")
    final override fun setCustomName(customName: Component?) {
        this.customName = customName
        isCustomNameVisible = customName != null
        if (customName == null) super.setCustomName(null)
        else super.setCustomName(Component.text()
            .append(customName)
            .append(Component.text(" "))
            .append(Component.translatable("mob.name.level", NamedTextColor.GOLD, Component.text(level)))
            .build())
    }
    fun getDisplayName() = super.getCustomName()

    fun damage(value: Float) {
        health -= value

        if (isDead) {
            kill()
            return
        }

        viewersAsAudience.playSound(hurtSound)

        val entityAnimationPacket = EntityAnimationPacket(entityId, EntityAnimationPacket.Animation.TAKE_DAMAGE)
        sendPacketToViewers(entityAnimationPacket)
    }

    fun kill() {
        viewersAsAudience.playSound(deathSound)

        triggerStatus(3)

        velocity = Vec.ZERO

        EventDispatcher.call(EntityDeathEvent(this))

        remove()
    }

    private val belowNameHologram = Hologram(null)

    var belowNameTagDefault: Component? = null
    set(value) {
        field = value
        belowNameTag = belowNameHologram.customName
    }

    var belowNameTag: Component?
    get() = belowNameHologram.customName
    set(text) {
        val newTag = text ?: belowNameTagDefault
        belowNameHologram.customName = newTag
    }

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

    private var ambientSoundDelay = 20

    override fun update(time: Long) {
        if (ambientSound != null) {
            --ambientSoundDelay
            if (ambientSoundDelay <= 0) {
                viewersAsAudience.playSound(ambientSound!!)
                ambientSoundDelay = random.nextInt(10, 40)
            }
        }
        if (viewers.isEmpty()) return
        if (velocity.isZero) return
        if (!belowNameHologram.isCustomNameVisible) return
        belowNameHologram.refreshPosition(hologramPosition())
    }

    override fun refreshPosition(newPosition: Pos) {
        super.refreshPosition(newPosition)
        belowNameHologram.refreshPosition(hologramPosition())
    }

    override fun remove() {
        super.remove()
        belowNameHologram.remove()
    }

    private fun hologramPosition() = position.add(.0, eyeHeight, .0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Entity) return false
        return other.uuid == this.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
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
        override val boundingBox get() = ce.boundingBox

        override val baseDefense get() = ce.baseDefense
        override val defense get() = ce.defense

        override fun getEleDefPercent(index: Int) = 0f

        override fun takeKnockback(a: Float, b: Double, c: Double) = ce.takeKnockback(a, b, c)

        override val viewersAsAudience get() = ce.viewersAsAudience

        override fun <T : Any?> getTag(tag: Tag<T>): T? = ce.getTag(tag)

        override fun <T : Any?> setTag(tag: Tag<T>, value: T?) = ce.setTag(tag, value)
    }

    open class Default(entityType: EntityType) : CustomEntity(entityType)
}

private val healthTag = Tag.Integer("health")