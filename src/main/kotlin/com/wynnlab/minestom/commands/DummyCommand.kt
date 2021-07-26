package com.wynnlab.minestom.commands

import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType

object DummyCommand : Command("dummy") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSyntax({ sender, _ ->
            val player = sender.asPlayer()
            val instance = player.instance ?: return@addSyntax

            val dummy = Entity(EntityType.VINDICATOR)
            dummy.setInstance(instance, player.position)
        })
    }
}