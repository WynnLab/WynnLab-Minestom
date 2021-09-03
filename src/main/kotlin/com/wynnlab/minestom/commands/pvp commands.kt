package com.wynnlab.minestom.commands

import net.minestom.server.command.CommandManager

fun registerPvpCommands(commandManager: CommandManager) {
    commandManager.register(PvpCommand)
    commandManager.register(HubCommand)
    commandManager.register(StatsCommand)
}

object PvpCommand : Command("pvp") {
    init {
        addSyntax({ sender, _ ->
            sender.sendMessage("Open PVP menu")
        })
    }
}

object HubCommand : Command("hub", "lobby", "l") {
    init {
        addSyntax({ sender, _ ->
            sender.sendMessage("Sending to hub")
        })
    }
}

object StatsCommand : Command("stats") {
    init {
        addSyntax({ sender, _ ->
            sender.sendMessage("Show stats")
        })
    }
}