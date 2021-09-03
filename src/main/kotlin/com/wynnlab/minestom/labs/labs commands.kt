package com.wynnlab.minestom.labs

import com.wynnlab.minestom.commands.Command
import com.wynnlab.minestom.commands.Subcommand
import com.wynnlab.minestom.isAllowed
import com.wynnlab.minestom.mainInstance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import java.util.*

fun registerLabsCommands(commandManager: CommandManager) {
    commandManager.register(LabCommand)
}

object LabCommand : Command(arrayOf(
    Component.text("WynnLab Labs:", NamedTextColor.GREEN),
    Component.text("/create", NamedTextColor.AQUA).append(Component.text(": Create your own lab", NamedTextColor.GRAY)),
    Component.text("/invite", NamedTextColor.AQUA).append(Component.text(": Invite a player to your lab", NamedTextColor.GRAY)),
    Component.text("/join", NamedTextColor.AQUA).append(Component.text(": Join a lab you have been invited to", NamedTextColor.GRAY)),
    Component.text("/leave", NamedTextColor.AQUA).append(Component.text(": Leave your current lab", NamedTextColor.GRAY)),
    Component.text("/transfer", NamedTextColor.AQUA).append(Component.text(": Transfer your lab", NamedTextColor.GRAY)),
    Component.text("/kick", NamedTextColor.AQUA).append(Component.text(": Kick someone from your lab", NamedTextColor.GRAY)),
    Component.text("/permissions", NamedTextColor.AQUA).append(Component.text(": Set the permission level of a player on your lab", NamedTextColor.GRAY)),
), "lab") {
    init {
        addSubcommand(Create)
        addSubcommand(Invite)
        addSubcommand(Join)
        addSubcommand(Leave)
        addSubcommand(Transfer)
        addSubcommand(Kick)
        addSubcommand(Permissions)
    }

    object Create : Subcommand("create") {
        init {
            setCondition { sender, _ -> sender.isPlayer }

            addSyntax({ sender, _ ->
                sender as Player
                if (getLab(sender) != null) {
                    sender.sendMessage("§cYou're already in your lab")
                    return@addSyntax
                }
                val lab = Lab(sender)
                sender.sendMessage("Created your lab")
                lab.join(sender)
            })
        }
    }

    object Invite : Subcommand("invite") {
        init {
            setCondition { sender, _ -> sender.isPlayer && sender.isAllowed(PERM_LABS_INVITE, 2) }

            val playerArg = ArgumentType.Word("player").setSuggestionCallback { sender, _, suggestion ->
                for (player in MinecraftServer.getConnectionManager().onlinePlayers) {
                    if (player.instance !is Lab || player.instance == sender.asPlayer().instance)
                        suggestion.addEntry(SuggestionEntry(player.username))
                }
            }

            addSyntax({ sender, ctx ->
                val player = MinecraftServer.getConnectionManager().getPlayer(ctx[playerArg])
                if (player == null) {
                    sender.sendMessage("§cThis player doesn't exist")
                    return@addSyntax
                }
                sender as Player

                (invites[player.uuid] ?: run { val set = hashSetOf<UUID>(); invites[player.uuid] = set; set }).add(sender.uuid)
                player.sendMessage(Component.text()
                    .append(Component.text(sender.username, NamedTextColor.AQUA))
                    .append(Component.text(" invited you to their lab. "))
                    .append(Component.text("Click here to join!", NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to join!")))
                        .clickEvent(ClickEvent.runCommand("/lab join ${sender.username}")))
                    .build())
            }, playerArg)
        }

        val invites = hashMapOf<UUID, HashSet<UUID>>()
    }

    object Join : Subcommand("join") {
        init {
            setCondition { sender, _ -> sender.isPlayer }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)

            addSyntax({ sender, ctx ->
                val player = ctx[playerArg].findFirstPlayer(sender)!!
                sender as Player
                if (Invite.invites[sender.uuid]?.contains(player.uuid) != true) {
                    sender.sendMessage("§c${player.username} did not invite you to their lab yet")
                }
                if (sender.instance is Lab) {
                    sender.sendMessage("§cYou are already in a lab. /lab leave to leave")
                    return@addSyntax
                }
                val lab = getLab(player)
                if (lab == null) {
                    sender.sendMessage("§cThe lab of ${player.username} is not running")
                    return@addSyntax
                }
                sender.setInstance(lab, Pos(.0, 42.0, .0))
            }, playerArg)
        }
    }

    object Leave : Subcommand("leave") {
        init {
            setCondition { sender, _ -> sender.isPlayer }

            addSyntax({ sender, _ ->
                sender as Player
                if (sender.instance !is Lab) {
                    sender.sendMessage("§cYou are not in a lab")
                    return@addSyntax
                }
                (sender.instance as Lab).leave(sender)
            })
        }
    }

    object Transfer : Subcommand("transfer") {
        init {
            setCondition { sender, _ -> sender.isPlayer && ((sender as Player).instance as? Lab?)?.owns(sender) == true }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)

            addSyntax({ sender, ctx ->
                val player = ctx[playerArg].findFirstPlayer(sender)!!
                sender as Player

                getLab(sender)!!.run {
                    sendMessage(Component.text("This lab was transferred to ${player.username}"))
                    transfer(player)
                }
            }, playerArg)
        }
    }

    object Kick : Subcommand("kick") {
        init {
            setCondition { sender, _ -> sender.isPlayer && sender.isAllowed(PERM_LABS_KICK, 3) }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)

            addSyntax({ sender, ctx ->
                val player = ctx[playerArg].findFirstPlayer(sender)!!

                sender.sendMessage("Kicked ${player.username} off the lab")
                player.sendMessage("You got kicked off the lab")
                player.setInstance(mainInstance, Pos(.0, 42.0, .0))
            }, playerArg)
        }
    }

    object Permissions : Subcommand("permissions", "perms") {
        init {
            setCondition { sender, _ -> sender.isPlayer && ((sender as Player).instance as? Lab?)?.owns(sender) == true }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)
            val levelArg = ArgumentType.Integer("level").between(0, 3)

            addSyntax({ sender, ctx ->
                val player = ctx[playerArg].findFirstPlayer(sender)!!
                val level = ctx[levelArg]
                sender as Player

                sender.sendMessage("${player.username} has now permission level $level on your lab")
                player.sendMessage("You now have permission level $level on this lab")
                (sender.instance as Lab).setPermissions(player, level)
            }, playerArg, levelArg)
        }
    }
}