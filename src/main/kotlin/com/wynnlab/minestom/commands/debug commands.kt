package com.wynnlab.minestom.commands

import com.wynnlab.minestom.players.prepareInventory
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command

fun registerDebugCommands(commandManager: CommandManager) {
    commandManager.register(RLInvCommand)
}

object RLInvCommand : Command("rl-inv") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSyntax({ sender, _ ->
            prepareInventory(sender.asPlayer())
        })
    }
}