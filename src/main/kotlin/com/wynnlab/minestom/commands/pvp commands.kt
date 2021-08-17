package com.wynnlab.minestom.commands

import net.minestom.server.command.CommandManager

fun registerPvpCommands(commandManager: CommandManager) {
    commandManager.register(PvpCommand)
    commandManager.register(HubCommand)
    commandManager.register(StatsCommand)
}

object PvpCommand : Command("Open the pvp menu", "pvp") {
    init {
        addSyntax({ sender, _ ->
            sender.sendMessage("Open PVP menu")
        })
    }
}

object HubCommand : Command("Go back to the lobby", "hub", "lobby", "l") {
    init {
        addSyntax({ sender, _ ->
            sender.sendMessage("Sending to hub")
        })
    }
}

object StatsCommand : Command("Show your stats", "stats") {
    init {
        addSyntax({ sender, _ ->
            sender.sendMessage("Show stats")
        })
    }
}