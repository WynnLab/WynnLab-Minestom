package com.wynnlab.minestom.classes.mage

import com.wynnlab.minestom.base.BasePlayerSpell
import com.wynnlab.minestom.core.damage.*
import com.wynnlab.minestom.particle.adventure.Particle
import com.wynnlab.minestom.particle.minestom.ParticleType
import com.wynnlab.minestom.particle.minestom.ParticleTypes
import com.wynnlab.minestom.util.*
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent

class MageMain(player: Player) : BasePlayerSpell(player, 0) {
    private val hit = mutableSetOf<DamageTarget>()

    override fun onCast() {
        val l1: Vec
        val l2: Vec
        run {
            val eyePos = player.eyePos
            l1 = eyePos.sub2Vec(0.5, 0.5, .0)
            l2 = (player.dirVec * 7.0 + eyePos).sub(.0, 0.5, .0)
        }

        sound(sound = tridentThrowSound)
        sound(sound = tridentRiptideSound)
        clone { sound(sound = shulkerShootSound) }

        for (v in l1..l2 step 0.5) {
            particle(v, if (isCloneSpell) squidInkParticle else cloudParticle)
            particle(v, if (isCloneSpell) witchParticle else critParticle)
            particle(v, enchantedHitParticle)

            bbTargets(v, 0.5) { t ->
                if (hit.add(t)) {
                    damageSource.attack(t, damageModifiers)
                }
            }
        }
    }
}

private val damageModifiers = DamageModifiers(false, 1f, NeutralConversion)

private val tridentThrowSound = Sound.sound(SoundEvent.ITEM_TRIDENT_THROW, Sound.Source.MASTER, 1f, 1.5f)
private val tridentRiptideSound = Sound.sound(SoundEvent.ITEM_TRIDENT_RIPTIDE_1, Sound.Source.MASTER, 0.2f, 1f)
private val shulkerShootSound = Sound.sound(SoundEvent.ENTITY_SHULKER_SHOOT, Sound.Source.MASTER, 0.5f, 1.5f)

private val o0s0 = ParticleTypes.OffsetAndSpeed(0f, 0f, 0f, 0f)
private val o0s0_1 = ParticleTypes.OffsetAndSpeed(0f, 0f, 0f, 0.1f)
private val squidInkParticle = Particle.particle(ParticleType.SQUID_INK, 2, o0s0_1)
private val cloudParticle = Particle.particle(ParticleType.CLOUD, 2, o0s0_1)
private val witchParticle = Particle.particle(ParticleType.WITCH, 1, o0s0)
private val critParticle = Particle.particle(ParticleType.CRIT, 2, o0s0)
private val enchantedHitParticle = Particle.particle(ParticleType.ENCHANTED_HIT, 1, o0s0_1)