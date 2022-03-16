package com.wynnlab.minestom.classes.mage

import com.wynnlab.minestom.base.BasePlayerSpell
import com.wynnlab.minestom.particle.adventure.Particle
import com.wynnlab.minestom.particle.minestom.ParticleType
import com.wynnlab.minestom.particle.minestom.ParticleTypes
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent

class Heal(player: Player) : BasePlayerSpell(player, duration = 2, cost = 6, period = 20) {
    override fun onTick() {
        particle(player.position.add(0.0, 0.5, 0.0), portalParticle)
        particle(player.position.add(0.0, 0.3, 0.0), enchantedParticle)
        sound(sound = evokerSound)
        sound(sound = lavaSound)

        val healer = player.name
        val amount = 50 //TODO
        heal(player, amount, healer)

        nearbyPlayers(player.position, 4.0) {
            heal(player, amount, healer)
        }
    }

    private fun heal(player: Player, amount: Int, by: Component) {
        player.sendMessage(Component.text()
            .append(Component.text('['))
            .append(by)
            .append(Component.text("] +50 <3"))
            .build()
        )
        // TODO: heal
        particle(player.position.add(0.0, 1.0, 0.0), sparkParticle)
    }
}

private val evokerSound = Sound.sound(SoundEvent.ENTITY_EVOKER_CAST_SPELL, Sound.Source.MASTER, 0.5f, 1.5f)
private val lavaSound = Sound.sound(SoundEvent.BLOCK_LAVA_EXTINGUISH, Sound.Source.MASTER, 1f, 1f)

private val areaOaS = ParticleTypes.OffsetAndSpeed(4f, 0f, 4f, 0.1f)
private val portalParticle = Particle.particle(ParticleType.PORTAL, 144, areaOaS)
private val enchantedParticle = Particle.particle(ParticleType.ENCHANTED_HIT, 144, areaOaS)
private val sparkParticle = Particle.particle(ParticleType.FIREWORK, 16, ParticleTypes.OffsetAndSpeed(0.3f, 1f, 0.3f, 0.05f))


/*
        amount = self.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() // 10
        PySpell.heal(self.player, amount)
        #self.player.sendMessage('{0}{1}{2}'.format(PySpell.colorText('[', '4'), PySpell.colorText('+{a}'.format(a=amount), 'c'), PySpell.colorText(']', '4')))

        for p in self.player.getNearbyEntities(4, 4, 4):
            if not isinstance(p, Player):
                continue

            PySpell.heal(p, amount)
            #p.sendMessage('{0}{1}{2}{3}'.format(PySpell.colorText('[', '4'), PySpell.colorText('+{a}'.format(a=amount), 'c'), PySpell.colorText(']', '4'), PySpell.colorText('({pl})'.format(self.player.getName()), '7')))
            Bukkit.getPluginManager().callEvent(EntityRegainHealthEvent(p, 50, EntityRegainHealthEvent.RegainReason.CUSTOM))

            self.particle(p.getLocation().clone().add(0, 1, 0), Particle.FIREWORKS_SPARK, 16, .3, 1, .3, .05)
 */