package com.wynnlab.minestom.core.damage

import com.wynnlab.minestom.entities.CustomEntity
import com.wynnlab.minestom.entities.Hologram
import com.wynnlab.minestom.tasks.RefreshDelayTask
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.utils.time.TimeUnit
import java.util.*
import kotlin.random.Random

fun damageIndicators(source: DamageSource, target: DamageTarget, finalDamage: Damage) {
    if (target.instance == null) return
    damageIndicatorHologram(target, finalDamage)
    healthIndicatorBelowName(target)
    if (source is DamageSource.Player)
        healthIndicatorBossBar(source.player, target)
}

private fun damageIndicatorHologram(target: DamageTarget, finalDamage: Damage) {
    val l = Pos(target.position)
        .add(random.nextDouble(-.3, .3), target.eyeHeight + random.nextDouble(1.0, 1.3), random.nextDouble(-.3, .3))

    val text = Component.text()
    var firstType = true

    for ((t, n) in finalDamage) {
        if (n == 0) continue
        if (!firstType) text.append(Component.text(" ")) else firstType = false

        text.append(Component.text("-$n${t.icon}", t.color))
    }

    val hologram = Hologram(text.build())
    hologram.setInstance(target.instance!!, l)

    MinecraftServer.getSchedulerManager().buildTask(hologram::remove).makeTransient().delay(1L, TimeUnit.SECOND)
        .schedule()
}

private fun healthIndicatorBelowName(target: DamageTarget) {
    if (target.isDead || target !is CustomEntity.DamageTarget) {
        //healthIndicatorsBelowName.remove(entity.uuid)?.remove()
        //entity.getTag(healthIndicatorBelowNameTag)?.cancel()
        return
    }

    val currentPercent = target.health / target.maxHealth

    val belowNameTag = Component.text()
        .append(Component.text("[", NamedTextColor.DARK_RED))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.08333f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.25f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.41667f, currentPercent)))
        .append(Component.text((target.health + .5f).toInt(), NamedTextColor.DARK_RED))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.58333f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.75f, currentPercent)))
        .append(Component.text("|", healthIndicatorBelowNameBarColor(.91667f, currentPercent)))
        .append(Component.text("]", NamedTextColor.DARK_RED))
        .build()

    target.ce.belowNameTag = belowNameTag

    RefreshDelayTask(target, "remove-hibn", target::removeHIBN).schedule(5, TimeUnit.SECOND)

    /*if (newIndicator) {
        val pos = Position()
        pos.set(entity.position)
        pos.add(.0, entity.eyeHeight, .0)
        indicator.setInstance(entity.instance!!, pos)
        /*entity.setTag(healthIndicatorBelowNameTag, MinecraftServer.getSchedulerManager().buildTask {
            putHealthIndicatorBelowName(entity.getAcquirable())
        }.repeat(1, TimeUnit.SERVER_TICK).schedule())*/

    }*/
}

private fun CustomEntity.DamageTarget.removeHIBN() {
    ce.belowNameTag = null
}

private fun healthIndicatorBossBar(source: Player, entity: DamageTarget) {
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

    RefreshDelayTask(source, "remove-hibb-${entity.uuid}", indicator, source::removeHIBB).schedule(5, TimeUnit.SECOND)
}

private fun Player.removeHIBB(indicator: BossBar) {
    hideBossBar(indicator)
}

private fun healthIndicatorBelowNameBarColor(percentReq: Float, currentPercent: Float) =
    if (currentPercent < percentReq) NamedTextColor.DARK_GRAY else NamedTextColor.RED

private fun bossBarText(entity: DamageTarget) = Component.text()
    .also { c -> entity.customName?.let { c
        .append(it)
        .append(Component.text(" - ", NamedTextColor.GRAY))
        .append(Component.text((entity.health + .5f).toInt(), NamedTextColor.RED))
        .append(Component.text("❤", NamedTextColor.DARK_RED))
        if (entity is CustomEntity.DamageTarget)
            entity.ce.belowNameTagDefault?.let { d ->
                c.append(Component.text(" - ", NamedTextColor.GRAY))
                c.append(d)
            }
    } }
    .build()

private fun bossBarProgress(entity: DamageTarget) = entity.health / entity.maxHealth

//private val healthIndicatorsBelowName = HashMap<UUID, Hologram>()
private val healthIndicatorsBossBar = HashMap<UUID, BossBar>()

//private val healthIndicatorBelowNameTag = Tag.Integer("task").map({ MinecraftServer.getSchedulerManager().getTask(it) }, { it.id })

private val random = Random(System.currentTimeMillis())