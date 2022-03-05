package com.wynnlab.minestom.commands

import com.wynnlab.minestom.*
import com.wynnlab.minestom.core.player.getId
import com.wynnlab.minestom.items.Identification
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.command.CommandManager
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Entity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemMeta
import net.minestom.server.item.ItemStack
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
    commandManager.register(TeleportCommand)
    //commandManager.register(EffectCommand)
    commandManager.register(GetDataCommand)
    commandManager.register(OpCommand)
    commandManager.register(PermissionCommand)
}

fun CommandSender.message(key: String, vararg args: ComponentLike) {
    val c = Component.text()
        .append(Component.text("[${if (this.isPlayer) this.asPlayer().username else if (this.isConsole) "Console" else "Unknown"}] "))
        .append(if (args.isEmpty()) Component.translatable(key) else Component.translatable(key, *args))
        .style {
            it.color(NamedTextColor.GRAY)
            it.decorate(TextDecoration.ITALIC)
        }
        .build()
    Audiences.console().sendMessage(c)
    val permLvl = if (this.isPlayer) this.asPlayer().permissionLevel else 4
    MinecraftServer.getConnectionManager().onlinePlayers.forEach {
        if (it.permissionLevel >= permLvl) it.sendMessage(c)
    }
}

val Player.component get() = name.hoverEvent(this)
val Entity.component get() = (customName ?: Component.text(uuid.toString())).hoverEvent(this)
val ItemStack.component get() = (displayName ?: Component.translatable("${if (material.isBlock) "block" else "item"}.${material.namespace().domain}.${material.namespace().path}"))

object StopCommand : Command("stop") {
    init {
        condition = isAllowed(PERM_SERVER_STOP, 4)
        addSyntax({ sender, _ ->
            sender.message("commands.stop.stopping")
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
            val player = ctx[playerArg].findFirstPlayer(sender)!!
            val message = LegacyComponentSerializer.legacy('&').deserialize(ctx.getRaw(messageArg))
            player.kick(message)
            sender.message("commands.kick.success", player.component, message)
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
            if (targets.size != 1) sender.message("commands.kill.success.multiple", Component.text(targets.size))
            else if (targets.size == 1) sender.message("commands.kill.success.single", targets[0].component)
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

        fun setGameMode(sender: CommandSender, players: List<Entity>, gameMode: GameMode) {
            players.forEach { (it as Player).gameMode = gameMode }
            val gameModeComponent = Component.translatable("gameMode.${gameMode.name.lowercase()}")
            if (players.size == 1) {
                val p = players[0]
                if (p == sender) sender.message("commands.gamemode.success.self", gameModeComponent)
                else sender.message("commands.gamemode.success.other", p.component, gameModeComponent)
            } else sender.message("commands.gamemode.success.multiple", Component.text(players.size), gameModeComponent)
        }

        addSyntax({ sender, ctx ->
            val targets = ctx[targetsArg].find(sender)
            val gameMode = GameMode.valueOf(ctx[gamemodeNameArg].uppercase())
            setGameMode(sender, targets, gameMode)
        }, gamemodeNameArg, targetsArg)

        addSyntax({ sender, ctx ->
            val targets = ctx[targetsArg].find(sender)
            val gameMode = GameMode.fromId(ctx[gamemodeIndexArg])!!
            setGameMode(sender, targets, gameMode)
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
            //sender.message("[${sender.signature}] Gave [$count x ${item.material.namespace()}] to [${players.size}] player(s)")
            if (players.size != 1) sender.message("commands.give.success.single", Component.text(count), item.component)
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
            //sender.message("[${sender.signature}] Changed block at [$position] to [${block.namespace()}]")
            sender.message("commands.setblock.success", Component.text(position.blockX()), Component.text(position.blockY()), Component.text(position.blockZ()))
        }, positionArg, blockArg)
    }
}

object TeleportCommand : Command("teleport", "tp") {
    init {
        setCondition { sender, _ -> sender.isPlayer && sender.isAllowed(PERM_SERVER_TP, 4) }

        val positionArg = ArgumentType.RelativeBlockPosition("position")
        val entitiesArg = ArgumentType.Entity("entities")
        val entityArg = ArgumentType.Entity("entity").singleEntity(true)

        addSyntax({ sender, ctx ->
            val position = ctx[positionArg].from(sender as Player).asPosition() // Please help me (Position not Block...)
            sender.teleport(position)
            //sender.message("[${sender.signature}] Teleported to [$position]")
            sender.message("commands.teleport.success.location.single", sender.component, Component.text(position.blockX()), Component.text(position.blockY()), Component.text(position.blockZ()))
        }, positionArg)

        addSyntax({ sender, ctx ->
            val entities = ctx[entitiesArg].find(sender as Player)
            val position = ctx[positionArg].from(sender).asPosition()
            entities.forEach { it.teleport(position) }
            if (entities.size != 1)
                sender.message("commands.teleport.success.location.single", Component.text(entities.size), Component.text(position.blockX()), Component.text(position.blockY()), Component.text(position.blockZ()))
            else
                sender.message("commands.teleport.success.location.multiple", entities[0].component, Component.text(position.blockX()), Component.text(position.blockY()), Component.text(position.blockZ()))
        }, entitiesArg, positionArg)

        addSyntax({ sender, ctx ->
            val entity = ctx[entityArg].findFirstEntity(sender as Player)!!
            sender.teleport(entity.position)
            //sender.message("[${sender.signature}] Teleported to [${entity.entityType}]")
            sender.message("commands.teleport.success.entity.single", sender.component, entity.component)
        }, entityArg)

        addSyntax({ sender, ctx ->
            val entities = ctx[entitiesArg].find(sender as Player)
            val entity = ctx[entityArg].findFirstEntity(sender)!!
            entities.forEach { it.teleport(entity.position) }
            //sender.message("[${sender.name}] Teleported [${entities.size}] entity/ies to [${entity.entityType}]")
            if (entities.size != 1) sender.message("commands.teleport.success.entity.multiple", Component.text(entities.size), entity.component)
            else sender.message("commands.teleport.success.entity.single", entities[0].component, entity.component)
        }, entitiesArg, entityArg)
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

        addSyntax({ sender, ctx ->
            val f = Entity::class.java.getDeclaredField("nbtCompound")
            f.isAccessible = true
            val nbtCompound = f[ctx.get<EntityFinder>("target").findFirstEntity(sender)!!] as NBTCompound
            sender.sendMessage(nbtCompound.toSNBT())
        }, ArgumentType.Literal("entity"), ArgumentType.Entity("target").singleEntity(true))

        addSyntax({ sender, _ ->
            val f = ItemMeta::class.java.getDeclaredField("nbt")
            f.isAccessible = true
            val nbtCompound = f[(sender as Player).itemInMainHand.meta] as NBTCompound
            sender.sendMessage(nbtCompound.toSNBT())
        }, ArgumentType.Literal("item"))

        addSyntax({ sender, ctx ->
            val id = ctx.get<Identification>("identification")
            sender.sendMessage(id.display.append(Component.text(": ${getId(sender as Player, id)}")))
        }, ArgumentType.Literal("id"), ArgumentType.Enum("identification", Identification::class.java))
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
                player.refreshCommands()
                //sender.message("[${sender.signature}] Changed OP level of [${player.username}] from [$levelFrom] to [$levelTo]")
                sender.message("commands.op.success.level", player.component, Component.text(levelFrom), Component.text(levelTo))
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

    object Grant : Subcommand("grant") {
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

    object Revoke : Subcommand("revoke") {
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
