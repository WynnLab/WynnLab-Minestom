package com.wynnlab.minestom.commands

import com.wynnlab.minestom.PERM_SERVER_PERMISSIONS
import com.wynnlab.minestom.PERM_SERVER_STOP
import com.wynnlab.minestom.isAllowed
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.permission.Permission

object StopCommand : Command("stop") {
    init {
        condition = isAllowed(PERM_SERVER_STOP)
        addSyntax({ sender, _ ->
            sender.sendMessage("Stopping the server")
            MinecraftServer.stopCleanly()
        })
    }
}

object PermissionCommand : Command("permission", "perm") {
    init {
        condition = isAllowed(PERM_SERVER_PERMISSIONS)
        addSubcommand(Grant)
        addSubcommand(Revoke)
    }

    object Grant : Command("grant") {
        init {
            val playerArg = ArgumentType.String("player")
            val permArg = ArgumentType.String("permission")

            addSyntax({ sender, context ->
                val player = MinecraftServer.getConnectionManager().getPlayer(context[playerArg])
                if (player == null) {
                    sender.sendMessage("§cPlayer not found")
                    return@addSyntax
                }
                val perm = context[permArg]
                player.addPermission(Permission(perm))
                sender.sendMessage("§aGranted permission")
            }, playerArg, permArg)
        }
    }

    object Revoke : Command("revoke") {
        init {
            val playerArg = ArgumentType.String("player")
            val permArg = ArgumentType.String("permission")

            addSyntax({ sender, context ->
                val player = MinecraftServer.getConnectionManager().getPlayer(context[playerArg])
                if (player == null) {
                    sender.sendMessage("§cPlayer not found")
                    return@addSyntax
                }
                val perm = context[permArg]
                player.removePermission(perm)
                sender.sendMessage("§aRevoked permission")
            }, playerArg, permArg)
        }
    }
}