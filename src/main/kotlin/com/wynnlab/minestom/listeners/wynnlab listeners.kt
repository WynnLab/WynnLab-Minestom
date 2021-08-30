package com.wynnlab.minestom.listeners

import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler

val wynnLabPlayerListenersNode = EventNode.type("wynnlab-player-listeners", EventFilter.PLAYER) { _, player ->
    player.gameMode == GameMode.ADVENTURE
}

fun initWynnLabListeners(globalEventHandler: GlobalEventHandler) {
    initClickListeners()
    initPlayerInventoryClickListeners()
    globalEventHandler.addChild(wynnLabPlayerListenersNode)
}