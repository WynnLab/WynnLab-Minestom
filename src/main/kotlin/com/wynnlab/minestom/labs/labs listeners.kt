package com.wynnlab.minestom.labs

import com.wynnlab.minestom.util.listen
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerDisconnectEvent

fun registerLabsListeners(globalEventHandler: GlobalEventHandler) {
    globalEventHandler.addChild(node)
    node.listen(::onPlayerDisconnect)
}

private val node = EventNode.event("labs-listeners", EventFilter.PLAYER) { e ->
    e is PlayerDisconnectEvent
}

private fun onPlayerDisconnect(event: PlayerDisconnectEvent) {
    val player = event.player
    if (player.instance is Lab) {
        (player.instance as Lab).leave(player)
    }
}