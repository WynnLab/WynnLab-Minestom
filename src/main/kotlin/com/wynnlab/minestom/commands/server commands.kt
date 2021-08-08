package com.wynnlab.minestom.commands

import com.wynnlab.minestom.*
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Entity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission
import net.minestom.server.utils.entity.EntityFinder
import org.jglrxavpok.hephaistos.nbt.NBTCompound

fun registerServerCommands(commandManager: CommandManager) {
    commandManager.register(StopCommand)
    commandManager.register(SaveAllCommand)
    commandManager.register(KickCommand)
    commandManager.register(KillCommand)
    //commandManager.register(VanishCommand)
    commandManager.register(GamemodeCommand)
    commandManager.register(GiveCommand)
    commandManager.register(SetblockCommand)
    //commandManager.register(EffectCommand)
    commandManager.register(GetDataCommand)
    commandManager.register(OpCommand)
    commandManager.register(PermissionCommand)
}

object StopCommand : Command("stop") {
    init {
        condition = isAllowed(PERM_SERVER_STOP, 4)
        addSyntax({ sender, _ ->
            sender.sendMessage("Stopping the server")
            stop()
        })
    }
}

object SaveAllCommand : Command("save-all") {
    init {
        condition = isAllowed(PERM_SERVER_SAVE_ALL, 4)
        addSyntax({ _, _ ->
            saveAll()
        })
    }
}

object KickCommand : Command("kick") {
    init {
        condition = isAllowed(PERM_SERVER_KICK, 4)

        val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)
        val messageArg = ArgumentType.StringArray("message")

        addSyntax({ sender, ctx ->
            ctx[playerArg].findFirstPlayer(sender)!!.kick(
                LegacyComponentSerializer.legacy('&').deserialize(ctx.getRaw(messageArg))
            )
        }, playerArg, messageArg)
    }
}

object KillCommand : Command("kill") {
    init {
        addConditionalSyntax({ sender, _ -> sender.isPlayer }, { sender, _ ->
            sender.asPlayer().kill()
        })

        val targetsArg = ArgumentType.Entity("targets")

        addConditionalSyntax(isAllowed(PERM_SERVER_KILL_OTHERS, 4), { sender, ctx ->
            val targets = ctx[targetsArg].find(sender)
            targets.forEach { if (it is Player) it.kill() else it.remove() }
        }, targetsArg)
    }
}

/*object VanishCommand : Command("vanish") {
    init {
        setCondition { sender, _ -> sender.isPlayer && (sender.asPlayer().permissionLevel >= 4 || sender.hasPermission(PERM_SERVER_VANISH)) }

        addSyntax({ sender, _ ->
            sender as Player
            sender.viewers.forEach(sender::removeViewer)
        })
    }
}*/

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

/*object EffectCommand : Command("effect") {
    init {
        condition = isAllowed(PERM_SERVER_EFFECT)

        addSubcommand(Give)
        addSubcommand(Clear)
    }

    object Give : Command("give") {
        init {
            val targetsArg = ArgumentType.Entity("targets").setDefaultValue(EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF))
            val typeArg = ArgumentType.Potion("type")
            val amplifierArg = ArgumentType.Integer("amplifier").between(0, 255)
            val durationArg = ArgumentType.Integer("duration").min(0)
            val particlesArg = ArgumentType.Boolean("particles").setDefaultValue(true)
            val iconArg = ArgumentType.Boolean("icon").setDefaultValue(true)
            val ambientArg = ArgumentType.Boolean("ambient").setDefaultValue(false)

            addSyntax({ sender, ctx ->
                ctx[targetsArg].find(sender).forEach {
                    it.addEffect(Potion(ctx[typeArg], ctx[amplifierArg].toByte(), ctx[durationArg], ctx[particlesArg], ctx[iconArg], ctx[ambientArg]))
                }
            }, targetsArg, typeArg, amplifierArg, durationArg, particlesArg, iconArg, ambientArg)
        }
    }

    object Clear : Command("clear") {
        init {
            val targetsArg = ArgumentType.Entity("targets")

            addSyntax({ sender, ctx ->
                ctx[targetsArg].find(sender).forEach(Entity::clearEffects)
            }, targetsArg)

            val typeArg = ArgumentType.Potion("type")

            addSyntax({ sender, ctx ->
                ctx[targetsArg].find(sender).forEach { it.removeEffect(ctx[typeArg]) }
            }, targetsArg, typeArg)
        }
    }
}*/

object GetDataCommand : Command("get-data") {
    init {
        setCondition { sender, _ -> sender.isPlayer }
        addSyntax({ sender, _ ->
            val f = Entity::class.java.getDeclaredField("nbtCompound")
            f.isAccessible = true
            val nbtCompound = f[sender] as NBTCompound
            sender.sendMessage(nbtCompound.toSNBT())
        })
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