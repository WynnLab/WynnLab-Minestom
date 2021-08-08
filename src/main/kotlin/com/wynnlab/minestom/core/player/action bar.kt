package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.core.damage.maxHealthTag
import com.wynnlab.minestom.listeners.clickSeqAbComponent
import com.wynnlab.minestom.listeners.clickSeqAbTag
import com.wynnlab.minestom.textColor
import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.TextComponent
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.utils.time.TimeUnit

fun scheduleActionBarTask() {
    MinecraftServer.getSchedulerManager().buildTask {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach { refreshActionBar(it, true) }
    }.repeat(1L, TimeUnit.SECOND).schedule()
}

/*fun registerItemChangeListener() {
    val node = EventNode.type("action-bar-item-change-listener", EventFilter.PLAYER) { e, p ->
        when (e) {
            is PlayerSwapItemEvent -> true
            is PlayerChangeHeldSlotEvent -> true
            else -> false
        }
    }
}*/

fun refreshActionBar(player: Player, scheduled: Boolean = false) {
    val clickSeqAbComponent = if (player.hasTag(clickSeqAbTag)) {
        if (scheduled) return
        clickSeqAbComponent(player)
    } else null
    val actionBar = Component.text()
    val mlD2 = if (clickSeqAbComponent == null) 0 else clickSeqAbComponent.content().length / 2

    val healthAbComponent = healthAbComponent(player)
    actionBar.append(healthAbComponent)
    if (clickSeqAbComponent != null) {
        actionBar.appendSpaces((20 - healthAbComponent.content().length - mlD2) / 2)
        actionBar.append(clickSeqAbComponent)
    } else actionBar.append(tenSpacesComponent)

    val manaAbComponent = manaAbComponent(player)
    if (mlD2 > 0) actionBar.appendSpaces((20 - manaAbComponent.content().length - mlD2) / 2)
    else actionBar.append(tenSpacesComponent)
    actionBar.append(manaAbComponent)

    player.sendActionBar(actionBar.build())
    /*if (clickSeqAbComponent != null) {
        val oldName = player.itemInMainHand.displayName
        player.setItemInHand(Player.Hand.MAIN, player.itemInMainHand.builder()
            .meta { it.set(itemDisplayNameTag,
                oldName?.let { it1 -> LegacyComponentSerializer.legacy('§').serialize(it1) })
            }
            .displayNameNonItalic(clickSeqAbComponent)
            .build())
    } else {
        val oldName = player.itemInMainHand.getTag(itemDisplayNameTag)
        player.setItemInHand(Player.Hand.MAIN, player.itemInMainHand.withDisplayName(
            oldName?.let { LegacyComponentSerializer.legacy('§').deserialize(it) }
        ))
    }*/
    //player.callItemUpdateStateEvent(Player.Hand.MAIN)
}

//private val itemDisplayNameTag = Tag.String("display-name-temp")

private fun healthAbComponent(player: Player): TextComponent {
    val maxHealth = player.getTag(maxHealthTag)!!
    return Component.text()
        .append(Component.text("[", 0xb0232f.textColor))
        .append(Component.text("❤ ", 0xd92b3a.textColor))
        .append(Component.text((player.health * maxHealth.toFloat() / 20f + .5f).toInt(), 0xe82738.textColor))
        .append(Component.text("/", 0xd92b3a.textColor))
        .append(Component.text(maxHealth, 0xe82738.textColor))
        .append(Component.text("]", 0xb0232f.textColor))
        .build()
}

private fun manaAbComponent(player: Player) = Component.text()
    .append(Component.text("[", 0x23abb0.textColor))
    .append(Component.text("✺ ", 0x2bd3d9.textColor))
    .append(Component.text(player.food, 0x23e1e8.textColor))
    .append(Component.text("/", 0x2bd3d9.textColor))
    .append(Component.text("20", 0x23e1e8.textColor))
    .append(Component.text("]", 0x23abb0.textColor))
    .build()

private fun <C : BuildableComponent<C, B>?, B : ComponentBuilder<C, B>?> ComponentBuilder<C, B>.appendSpaces(n: Int): ComponentBuilder<C, B> {
    when (n) {
        0 -> {}
        1 -> append(Component.text(" "))
        10 -> append(tenSpacesComponent)
        else -> append(Component.text((StringBuffer().apply { repeat(n) { append(' ') } }).toString()))
    }
    return this
}

private const val tenSpaces = "          "
private val tenSpacesComponent = Component.text(tenSpaces)