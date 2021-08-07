package com.wynnlab.minestom.listeners

import com.wynnlab.minestom.core.damage.NeutralDamageModifiers
import com.wynnlab.minestom.core.damage.attack
import com.wynnlab.minestom.gui.MenuGui
import com.wynnlab.minestom.tasks.RefreshDelayTask
import com.wynnlab.minestom.util.listen
import com.wynnlab.minestom.util.rayCastEntity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.tag.Tag
import net.minestom.server.utils.time.TimeUnit

val playerSpellClickListenersNode = EventNode.type("player-spell-click-listeners", EventFilter.PLAYER) { e, player ->
    e is PlayerBlockInteractEvent && e.hand == Player.Hand.MAIN
            || e is PlayerUseItemEvent && e.hand == Player.Hand.MAIN
            || e is PlayerHandAnimationEvent && e.hand == Player.Hand.MAIN && player.openInventory == null
}

private fun onPlayerHandAnimation(e: PlayerHandAnimationEvent) {
    e.isCancelled = true
    onPlayerLeftClick(e.player)
}

private fun onPlayerUseItem(e: PlayerUseItemEvent) {
    e.isCancelled = true
    onPlayerRightClick(e.player)
}

private fun onPlayerRightClick(player: Player) {
    when (player.heldSlot.toInt()) {
        6 -> MenuGui().show(player)
        7, 8 -> {}
        else -> {
            noAb(player)
            val seq = addToClickSequence(player, true)
            showClickSequence(player, seq)
            val spell = when (seq) {
                16.toByte() /* rlr */ -> 1
                13.toByte() /* rrr */ -> 2
                else -> -1
            }
            if (spell > 0) castSpellAndResetClickSequence(player, spell)
        }
    }
}

private fun onPlayerLeftClick(player: Player) {
    if (player.heldSlot > 5) return
    val seq = addToClickSequence(player, false)
    if (seq == 2.toByte()) // 1x left-click
        castSpellAndResetClickSequence(player, 0)
    else {
        noAb(player)
        showClickSequence(player, seq)
        val spell = when (seq) {
            25.toByte() /* rll */ -> 3
            22.toByte() /* rrl */ -> 4
            else -> -1
        }
        if (spell > 0) castSpellAndResetClickSequence(player, spell)
    }
}

private fun addToClickSequence(player: Player, rightClick: Boolean): Byte {
    val count = if (rightClick) 1 else 2
    val prev = player.getTag(clickSequenceTag)!!.toInt()
    val new = (prev + (if (prev == 0) count else if (prev >= 3) count * 3 * 3 else count * 3)).toByte()
    player.setTag(clickSequenceTag, new)
    return new
}

private fun showClickSequence(player: Player, sequence: Byte) {
    val c1 = sequence % 3
    val c2 = (sequence / 3) % 3
    val c3 = sequence / 9
    val hyphen = Component.text("-", NamedTextColor.GRAY)
    player.sendActionBar(Component.text()
        .append(clickSequenceAbChar(c1))
        .append(hyphen)
        .append(clickSequenceAbChar(c2))
        .append(hyphen)
        .append(clickSequenceAbChar(c3))
        .build())
}

private fun clickSequenceAbChar(c: Int) = when (c) {
    1 -> Component.text("R", NamedTextColor.GREEN, TextDecoration.UNDERLINED)
    2 -> Component.text("L", NamedTextColor.GREEN, TextDecoration.UNDERLINED)
    else -> Component.text("?", NamedTextColor.WHITE)
}

// stored as base 3 value: r = 1, l = 2
// r-?-? = 1
// r-r-? = 4; r-l-? = 7
private val clickSequenceTag = Tag.Byte("click-sequence").defaultValue(0)

private fun noAb(player: Player) {
    player.setTag(noAbTag, 1)
    RefreshDelayTask(player, "no-ab") {
        player.removeTag(noAbTag)
        resetClickSequence(player)
    }.schedule(1, TimeUnit.SECOND)
}

private val noAbTag = Tag.Byte("no-ab")

private fun castSpellAndResetClickSequence(player: Player, spell: Int) {
    if (spell == 0) player.rayCastEntity(maxDistance = 4.0) { it is LivingEntity }?.let { player.attack(it as LivingEntity, NeutralDamageModifiers) }
    //player.setGravity(player.gravityDragPerTick / 2f, player.gravityAcceleration / 2f)
    player.sendMessage("spell $spell")
    resetClickSequence(player)
}

private fun resetClickSequence(player: Player) {
    player.setTag(clickSequenceTag, 0)
}

fun initClickListeners() {
    playerSpellClickListenersNode.listen(::onPlayerHandAnimation)
    playerSpellClickListenersNode.listen(::onPlayerUseItem)

    wynnLabPlayerListenersNode.addChild(playerSpellClickListenersNode)
}