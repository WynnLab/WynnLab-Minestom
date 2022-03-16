package com.wynnlab.minestom.classes.mage

import com.wynnlab.minestom.base.BasePlayerSpell
import com.wynnlab.minestom.core.damage.DamageTarget
import com.wynnlab.minestom.util.dirVec
//import com.wynnlab.minestom.util.rayCast
import net.minestom.server.entity.Player

class Teleport(player: Player) : BasePlayerSpell(player, duration = 0, cost = 4) {
    private val hit = mutableSetOf<DamageTarget>()

    override fun onCast() {
        /*val instance = player.instance ?: return
        val rayTarget = player.rayCast(player.dirVec, 14.0) {
            if (!instance.getBlock(it).isAir) it
            else {
                for (e in instance.getChunkEntities(instance.getChunkAt(it))) {
                    if (e == player) continue
                    if (e.boundingBox.intersect(it)) return@rayCast e
                }
                null
            }
        }*/
    }
}

/*
class Spell(PySpell):
    def __init__(self):
        self.hit = set()

    def tick(self):
        ray = self.player.rayTraceBlocks(14)
        target = self.player.getLocation().clone().add(self.player.getEyeLocation().getDirection().clone().multiply(14)) if ray is None or ray.getHitBlock() is None else ray.getHitPosition().toLocation(self.player.getWorld())
        while not target.getBlock().isPassable():
            target.add(self.player.getEyeLocation().getDirection().clone().multiply(-1))
        target.setDirection(self.player.getEyeLocation().getDirection())

        particle_start = self.player.getEyeLocation()
        particle_dir = particle_start.getDirection()
        self.player.teleport(target)

        for l in LocationIterator(particle_start, target, .5):
            self.particle(l.clone().subtract(0, 1, 0) if self.clone else l, Particle.DRIP_LAVA if self.clone else Particle.FLAME, 1, 0, 0, 0, 0)

        for l in LocationIterator(particle_start, target, 1):
            self.particle(l, Particle.VILLAGER_ANGRY if self.clone else Particle.LAVA, 1, 0, 0, 0, 0)

            for e in self.nearbyMobs(l, .5, 2, .5):
                if e in self.hit:
                    continue
                self.hit.add(e)

                self.damage(e, False, 1, .6, 0, .4, 0, 0, 0)

        self.sound(Sound.ENTITY_ENDERMAN_TELEPORT if self.clone else Sound.ENTITY_SHULKER_TELEPORT, 1, 1)
        self.sound(target, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1, 1)
 */