package com.wynnlab.minestom.commands

import com.wynnlab.minestom.*
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Entity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission
import net.minestom.server.utils.entity.EntityFinder
import kotlin.system.exitProcess

fun registerServerCommands(commandManager: CommandManager) {
    commandManager.register(StopCommand)
    commandManager.register(GamemodeCommand)
    commandManager.register(GiveCommand)
    commandManager.register(SetblockCommand)
    commandManager.register(OpCommand)
    commandManager.register(PermissionCommand)
}

object StopCommand : Command("stop") {
    init {
        condition = isAllowed(PERM_SERVER_STOP, 4)
        addSyntax({ sender, _ ->
            sender.sendMessage("Stopping the server")
            MinecraftServer.stopCleanly()
            exitProcess(0)
        })
    }
}

object GamemodeCommand : Command("gamemode", "gm") {
    init {
        condition = isAllowed(PERM_SERVER_GAMEMODE)

        val gamemodeNameArg = ArgumentType.Word("gamemode").from("survival", "creative", "adventure", "spectator")
        val gamemodeIndexArg = ArgumentType.Integer("gamemode").between(0, 3)
        val targetsArg = ArgumentType.Entity("targets").onlyPlayers(true)
            .setDefaultValue { EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF) }

        fun setGameMode(players: List<Entity>, gameMode: GameMode) {
            players.forEach { (it as Player).gameMode = gameMode }
        }

        addSyntax({ sender, ctx ->
            setGameMode(ctx[targetsArg].find(sender), GameMode.valueOf(ctx[gamemodeNameArg].uppercase()))
        }, gamemodeNameArg, targetsArg)

        addSyntax({ sender, ctx ->
            setGameMode(ctx[targetsArg].find(sender), GameMode.fromId(ctx[gamemodeIndexArg].toByte())!!)
        }, gamemodeIndexArg, targetsArg)
    }
}

object GiveCommand : Command("give") {
    init {
        condition = isAllowed(PERM_SERVER_GIVE)

        val targetsArg = ArgumentType.Entity("targets").onlyPlayers(true)
        val itemArg = ArgumentType.ItemStack("item")
        val countArg = ArgumentType.Integer("count").min(1).setDefaultValue(1)

        addSyntax({ sender, ctx ->
            val players = ctx[targetsArg].find(sender)
            if (players.isEmpty()) return@addSyntax
            var item = ctx[itemArg]
            val count = ctx[countArg]
            if (count > 1) item = item.withAmount(count)
            players.forEach { (it as Player).inventory.addItemStack(item) }
        }, targetsArg, itemArg, countArg)
    }
}

object SetblockCommand : Command("setblock") {
    init {
        setCondition { sender, _ -> !sender.isConsole && sender.isAllowed(PERM_SERVER_SETBLOCK) }

        val positionArg = ArgumentType.RelativeBlockPosition("position")
        val blockArg = ArgumentType.BlockState("block")

        addSyntax({ sender, ctx ->
            val player = sender.asPlayer()
            val relPos = ctx[positionArg]
            val position = relPos.from(player)
            val block = ctx[blockArg]
            player.instance?.setBlock(position, block)
        }, positionArg, blockArg)
    }
}

object OpCommand : Command("op") {
    init {
        setCondition { sender, _ -> sender.isConsole || sender.isPlayer && sender.asPlayer().permissionLevel > 0 }

        val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)
        val levelArg = ArgumentType.Integer("level").between(0, 4)

        addSyntax({ sender, ctx ->
            val player = ctx[playerArg].findFirstPlayer(sender)!!
            val levelTo = ctx[levelArg]
            val levelFrom = player.permissionLevel
            val senderLevel = if (sender.isConsole) 5 else (sender as Player).permissionLevel
            val allowed = when {
                levelTo > levelFrom -> levelTo <= senderLevel
                levelTo < levelFrom -> sender == player || levelFrom < senderLevel
                else -> true
            }
            if (!allowed)
                sender.sendMessage("You are not allowed to change the permission level of ${player.username} to $levelTo")
            else {
                sender.sendMessage("${player.username} has now permission level $levelTo")
                player.permissionLevel = levelTo
            }
        }, playerArg, levelArg)
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
            val playersArg = ArgumentType.Entity("targets").onlyPlayers(true)
            val permArg = ArgumentType.String("permission")

            addSyntax({ sender, ctx ->
                val players = ctx[playersArg].find(sender)
                val perm = ctx[permArg]
                players.forEach { it.addPermission(Permission(perm)) }
                sender.sendMessage("Granted permissions")
            }, playersArg, permArg)
        }
    }

    object Revoke : Command("revoke") {
        init {
            val playersArg = ArgumentType.Entity("targets").onlyPlayers(true)
            val permArg = ArgumentType.String("permission")

            addSyntax({ sender, ctx ->
                val players = ctx[playersArg].find(sender)
                val perm = ctx[permArg]
                players.forEach { it.removePermission(perm) }
                sender.sendMessage("Revoked permission")
            }, playersArg, permArg)
        }
    }
}