package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.core.damage.playerMaxHealthTag
import com.wynnlab.minestom.listeners.clickSeqAbComponent
import com.wynnlab.minestom.textColor
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.listen
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.inventory.itemStacksRaw
import net.minestom.server.item.builder
import net.minestom.server.utils.time.TimeUnit

fun initActionBar() {
    scheduleActionBarTask()
    //registerItemChangeListener()
}

private fun scheduleActionBarTask() {
    MinecraftServer.getSchedulerManager().buildTask {
        MinecraftServer.getConnectionManager().onlinePlayers.forEach { refreshActionBar(it) }
    }.repeat(1L, TimeUnit.SECOND).schedule()
}

/*private fun registerItemChangeListener() {
    val node = EventNode.event("action-bar-item-change-listener", EventFilter.PLAYER) { e ->
        e is PlayerSwapItemEvent || e is PlayerChangeHeldSlotEvent// || e is InventoryOpenEvent
    }
    node.listen { it.entity.inventory.update() }
    MinecraftServer.getGlobalEventHandler().addChild(node)
}*/

fun refreshActionBar(player: Player) {
    val actionBar = Component.text()

    val healthAbComponent = healthAbComponent(player)
    actionBar.append(healthAbComponent)
    actionBar.append(twentySpacesComponent)

    val manaAbComponent = manaAbComponent(player)
    actionBar.append(manaAbComponent)

    player.sendActionBar(actionBar.build())
}

fun refreshClickSequenceBar(player: Player) {
    val clickSeqAbComponent = clickSeqAbComponent(player)

    val oldItem = player.itemInMainHand
    player.itemInMainHand = player.itemInMainHand.builder()
        .displayNameNonItalic(clickSeqAbComponent)
        .build()
    player.scheduleNextTick { (it as Player).inventory.itemStacksRaw[it.heldSlot.toInt()] = oldItem }
}

fun resetClickSequenceBar(player: Player) {
    //val oldItem = player.itemInMainHand
    //player.itemInMainHand = player.itemInMainHand.withDisplayName(Component.empty())
    //player.inventory.itemStacksRaw[player.heldSlot.toInt()] = oldItem
    //player.itemInMainHand = oldItem
    player.inventory.update()
}

/*fun refreshActionBar(player: Player, scheduled: Boolean) {
    val actionBar = Component.text()
    //val mlD2 = if (clickSeqAbComponent == null) 0 else clickSeqAbComponent.content().length / 2

    val healthAbComponent = healthAbComponent(player)
    actionBar.append(healthAbComponent)
    /*if (clickSeqAbComponent != null) {
        actionBar.appendSpaces((20 - healthAbComponent.content().length - mlD2) / 2)
        actionBar.append(clickSeqAbComponent)
    } else actionBar.append(tenSpacesComponent)*/
    actionBar.append(twentySpacesComponent)

    val manaAbComponent = manaAbComponent(player)
    /*if (mlD2 > 0) actionBar.appendSpaces((20 - manaAbComponent.content().length - mlD2) / 2)
    else actionBar.append(tenSpacesComponent)*/
    actionBar.append(manaAbComponent)

    player.sendActionBar(actionBar.build())


    val clickSeqAbComponent = if (player.hasTag(clickSeqAbTag)) {
        if (scheduled) return
        clickSeqAbComponent(player)
    } else null

    if (clickSeqAbComponent != null) {
        val oldItem = player.itemInMainHand
        player.itemInMainHand = player.itemInMainHand.builder()
            .displayNameNonItalic(clickSeqAbComponent)
            .build()
        //player.callItemUpdateStateEvent(Player.Hand.MAIN)
        player.inventory.itemStacksRaw[player.heldSlot.toInt()] = oldItem
    } else {
        //player.itemInMainHand = player.itemInMainHand.withDisplayName(Component.empty())
    /*else {
        val oldName = player.itemInMainHand.getTag(itemDisplayNameTag)
        player.setItemInHand(Player.Hand.MAIN, player.itemInMainHand.withDisplayName(
            oldName?.let { LegacyComponentSerializer.legacy('§').deserialize(it) }
        ))
    }*/
    }
    //player.callItemUpdateStateEvent(Player.Hand.MAIN)
}*/

//private val itemDisplayNameTag = Tag.String("display-name-temp")

private fun healthAbComponent(player: Player): TextComponent {
    val maxHealth = player.getTag(playerMaxHealthTag)!!
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

/*private fun <C : BuildableComponent<C, B>?, B : ComponentBuilder<C, B>?> ComponentBuilder<C, B>.appendSpaces(n: Int): ComponentBuilder<C, B> {
    when (n) {
        0 -> {}
        1 -> append(Component.text(" "))
        10 -> append(tenSpacesComponent)
        else -> append(Component.text((StringBuffer().apply { repeat(n) { append(' ') } }).toString()))
    }
    return this
}*/

private const val tenSpaces = "          "
private const val twentySpaces = tenSpaces + tenSpaces
//private val tenSpacesComponent = Component.text(tenSpaces)
private val twentySpacesComponent = Component.text(twentySpaces)