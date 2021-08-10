package com.wynnlab.minestom.labs

import com.wynnlab.minestom.isAllowed
import com.wynnlab.minestom.mainInstance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.minestom.server.utils.Position
import java.util.*
import kotlin.collections.HashSet

fun registerLabsCommands(commandManager: CommandManager) {
    commandManager.register(LabCommand)
}

object LabCommand : Command("lab") {
    init {
        addSubcommand(Create)
        addSubcommand(Invite)
        addSubcommand(Join)
        addSubcommand(Leave)
        addSubcommand(Transfer)
        addSubcommand(Kick)
        addSubcommand(Permissions)
    }

    object Create : Command("create") {
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

    object Invite : Command("invite") {
        init {
            setCondition { sender, _ -> sender.isPlayer && sender.isAllowed(PERM_LABS_INVITE, 2) }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)

            addSyntax({ sender, ctx ->
                val player = ctx[playerArg].findFirstPlayer(sender)!!
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

    object Join : Command("join") {
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
                sender.setInstance(lab, Position(.0, 42.0, .0))
            }, playerArg)
        }
    }

    object Leave : Command("leave") {
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

    object Transfer : Command("transfer") {
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

    object Kick : Command("kick") {
        init {
            setCondition { sender, _ -> sender.isPlayer && sender.isAllowed(PERM_LABS_KICK, 3) }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)

            addSyntax({ sender, ctx ->
                val player = ctx[playerArg].findFirstPlayer(sender)!!

                sender.sendMessage("Kicked ${player.username} off the lab")
                player.sendMessage("You got kicked off the lab")
                player.setInstance(mainInstance, Position(.0, 42.0, .0))
            }, playerArg)
        }
    }

    object Permissions : Command("permissions", "perms") {
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