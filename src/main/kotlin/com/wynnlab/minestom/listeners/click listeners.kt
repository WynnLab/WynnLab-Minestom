package com.wynnlab.minestom.listeners

import com.wynnlab.minestom.util.listen
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.*

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
    player.sendMessage("RC")
}

private fun onPlayerLeftClick(player: Player) {
    player.sendMessage("LC")
}

fun initClickListeners() {
    playerSpellClickListenersNode.listen(::onPlayerHandAnimation)
    playerSpellClickListenersNode.listen(::onPlayerUseItem)

    wynnLabPlayerListenersNode.addChild(playerSpellClickListenersNode)
}