package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.entities.Hologram
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.acquirable.Acquirable
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Position
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

fun damageIndicators(source: Player, entity: LivingEntity, finalDamage: Damage) {
    if (entity.instance == null) return
    damageIndicatorHologram(entity, finalDamage)
    healthIndicatorBelowName(entity)
    healthIndicatorBossBar(source, entity)
}

private fun damageIndicatorHologram(entity: LivingEntity, finalDamage: Damage) {
    val l = Position().apply { set(entity.position) }
        .add(random.nextDouble(-.3, .3), entity.eyeHeight + random.nextDouble(1.0, 1.3), random.nextDouble(-.3, .3))

    val text = Component.text()
    var firstType = true

    for ((t, n) in finalDamage) {
        if (n == 0) continue
        if (!firstType) text.append(Component.text(" ")) else firstType = false

        text.append(Component.text("-$n${t.icon}", t.color))
    }

    val hologram = Hologram(text.build())
    hologram.setInstance(entity.instance!!, l)

    MinecraftServer.getSchedulerManager().buildTask(hologram::remove).makeTransient().delay(1L, TimeUnit.SECOND)
        .schedule()
}

private fun healthIndicatorBelowName(entity: LivingEntity) {
    if (entity.isDead) {
        healthIndicatorsBelowName.remove(entity.uuid)?.remove()
        entity.getTag(healthIndicatorBelowNameTag)?.cancel()
        return
    }

    var newIndicator = false
    val indicator = healthIndicatorsBelowName[entity.uuid]
        ?: Hologram(null).also { newIndicator = true; healthIndicatorsBelowName[entity.uuid] = it }

    val currentPercent = entity.health / 20f

    val customName = Component.text()
        .append(Component.text("[", NamedTextColor.DARK_RED))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.08333f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.25f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.41667f, currentPercent)))
        .append(Component.text((entity.health * entity.getTag(maxHealthTag)!! / 20f + .5f).toInt(), NamedTextColor.DARK_RED))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.58333f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.75f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.91667f, currentPercent)))
        .append(Component.text("]", NamedTextColor.DARK_RED))
        .build()

    indicator.customName = customName

    if (newIndicator) {
        val pos = Position()
        pos.set(entity.position)
        pos.add(.0, entity.eyeHeight, .0)
        indicator.setInstance(entity.instance!!, pos)
        entity.setTag(healthIndicatorBelowNameTag, MinecraftServer.getSchedulerManager().buildTask {
            putHealthIndicatorBelowName(entity.getAcquirable())
        }.repeat(1, TimeUnit.SERVER_TICK).schedule())
    }
}

private fun healthIndicatorBossBar(source: Player, entity: LivingEntity) {
    if (entity.isDead) {
        healthIndicatorsBossBar.remove(entity.uuid)?.let {
            MinecraftServer.getBossBarManager().destroyBossBar(it)
        }
        return
    }

    var newIndicator = false
    val indicator = healthIndicatorsBossBar[entity.uuid]
        ?: BossBar.bossBar(bossBarText(entity), bossBarProgress(entity), BossBar.Color.RED, BossBar.Overlay.NOTCHED_10)
            .also { newIndicator = true; healthIndicatorsBossBar[entity.uuid] = it }

    if (!newIndicator)
        indicator.name(bossBarText(entity)).progress(bossBarProgress(entity))

    source.showBossBar(indicator)
}

private fun putHealthIndicatorBelowName(entity: Acquirable<Entity>) = entity.async {
    val pos = Position()
    pos.set(it.position)
    pos.add(.0, it.eyeHeight, .0)
    healthIndicatorsBelowName[it.uuid]?.refreshPosition(pos)
}

private fun healthIndicatorBelowNameBarColor(percentReq: Float, currentPercent: Float) =
    if (currentPercent < percentReq) NamedTextColor.DARK_GRAY else NamedTextColor.RED

private fun bossBarText(entity: LivingEntity) = Component.text()
    .also { c -> entity.customName?.let { c
        .append(it)
        .append(Component.text(" - "))
        .append(Component.text((entity.health * entity.getTag(maxHealthTag)!! / 20f + .5f).toInt()))
    } }
    .build()

private fun bossBarProgress(entity: LivingEntity) = entity.health / 20f

private val healthIndicatorsBelowName = HashMap<UUID, Hologram>()
private val healthIndicatorsBossBar = HashMap<UUID, BossBar>()

private val healthIndicatorBelowNameTag = Tag.Integer("task").map({ MinecraftServer.getSchedulerManager().getTask(it) }, { it.id })

private val random = Random(System.currentTimeMillis())