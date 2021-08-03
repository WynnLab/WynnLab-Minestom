package com.wynnlab.minestom.commands

import com.wynnlab.minestom.players.Rank
import com.wynnlab.minestom.players.prepareInventory
import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

fun registerDebugCommands(commandManager: CommandManager) {
    commandManager.register(RLInvCommand)
    commandManager.register(RankCommand)
}

object RLInvCommand : Command("rl-inv") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSyntax({ sender, _ ->
            prepareInventory(sender.asPlayer())
        })
    }
}

object RankCommand : Command("rank") {
    init {
        setCondition { sender, _ -> sender.isPlayer && sender.asPlayer().permissionLevel >= 4 }

        val rankArg = ArgumentType.Enum("rank", Rank::class.java)

        addSyntax({ sender, ctx ->
            sender as Player
            val rank = ctx[rankArg]
            val name = Component.text()
            rank.tag?.let { name.append(it).append(Component.text(" ")) }
            name.append(Component.text(sender.username, rank.nameColor))
            sender.displayName = name.build()
        }, rankArg)
    }
}