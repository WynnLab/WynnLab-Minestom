package com.wynnlab.minestom.commands

import com.wynnlab.minestom.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player

fun registerEssentialsCommands(commandManager: CommandManager) {
    commandManager.register(MessageCommand)
    commandManager.register(ReplyCommand)
    commandManager.register(PartyCommand)
    commandManager.register(PartyMessageCommand)
}

object MessageCommand : Command("message", "msg") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        val targetsArg = ArgumentType.Entity("targets").onlyPlayers(true)
        val messageArg = ArgumentType.StringArray("message")

        addSyntax({ sender, ctx ->
            pm(sender as Player, ctx[targetsArg].find(sender), ctx.getRaw(targetsArg), ctx.getRaw(messageArg))
        }, targetsArg, messageArg)
    }
}

object ReplyCommand : Command("reply", "r") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        val messageArg = ArgumentType.StringArray("message")

        addSyntax({ sender, ctx ->
            sender as Player
            val receiver = conversations[sender] ?: run {
                sender.sendMessage("No conversation")
                return@addSyntax
            }
            pm(sender, listOf(receiver), receiver.username, ctx.getRaw(messageArg))
        }, messageArg)
    }
}

object PartyCommand : Command(arrayOf(
    Component.translatable("command.party.description", NamedTextColor.GREEN),
    Component.text("/create: ", NamedTextColor.AQUA).append(Component.translatable("command.party.create.description", NamedTextColor.GRAY)),
    Component.text("/invite: ", NamedTextColor.AQUA).append(Component.translatable("command.party.invite.description", NamedTextColor.GRAY)),
    Component.text("/join: ", NamedTextColor.AQUA).append(Component.translatable("command.party.join.description", NamedTextColor.GRAY)),
    Component.text("/leave: ", NamedTextColor.AQUA).append(Component.translatable("command.party.leave.description", NamedTextColor.GRAY)),
    Component.text("/message: ", NamedTextColor.AQUA).append(Component.translatable("command.party.message.description", NamedTextColor.GRAY)),
), "party") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSubcommand(Create)
        addSubcommand(Invite)
        addSubcommand(Join)
        addSubcommand(Leave)
        addSubcommand(Message)
    }

    object Create : Subcommand("create") {
        init {
            val nameArg = ArgumentType.String("name")

            addSyntax({ sender, _ ->
                sender as Player
                execute(sender, "${sender.username}'s Party")
            })

            addSyntax({ sender, ctx ->
                execute(sender as Player, ctx[nameArg])
            }, nameArg)
        }

        private fun execute(sender: Player, name: String) {
            sender.sendMessage("Create party $name")
        }
    }

    object Invite : Subcommand("invite") {
        init {
            setCondition { sender, _ -> sender.isPlayer }

            val playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)

            addSyntax({ sender, ctx ->
                val target = ctx[playerArg].findFirstPlayer(sender)!!
                sender.sendMessage("Invite ${target.username} to party")
            }, playerArg)
        }
    }

    object Join : Subcommand("join") {
        init {
            setCondition { sender, _ -> sender.isPlayer }

            val nameArg = ArgumentType.String("name")

            addSyntax({ sender, _ ->
                sender.sendMessage("Join party (last invite)")
            })

            addSyntax({ sender, ctx ->
                sender.sendMessage("Join party ${ctx[nameArg]}")
            }, nameArg)
        }
    }

    object Leave : Subcommand("leave")

    object Message : Subcommand("message") {
        init {
            partyMessageInit()
        }
    }
}

object PartyMessageCommand : Command("Send a message to your party", "p") {
    init {
        partyMessageInit()
    }
}

private fun Command.partyMessageInit() {
    setCondition { sender, _ -> sender.isPlayer }

    val messageArg = ArgumentType.StringArray("message")

    addSyntax({ sender, ctx ->
        sender.sendMessage("Party message: ${ctx.getRaw(messageArg)}")
    }, messageArg)
}

private val conversations = mutableMapOf<Player, Player>()

private fun pm(sender: Player, receivers: List<Entity>, specifiedReceiver: String, text: String) {
    val you = Component.text("You", COLOR_WYNN.textColor)
    val senderComponent = sender.name.colorIfAbsent(COLOR_AQUA.textColor)
    val textComponent = LegacyComponentSerializer.legacy('&').deserialize(text)

    sender.sendMessage(pmComponent(you, Component.text(specifiedReceiver, (if (specifiedReceiver.startsWith('@')) COLOR_AQUA else COLOR_PURPLE).textColor), textComponent))
    for (receiver in receivers) {
        receiver as Player
        receiver.sendMessage(pmComponent(senderComponent, you, textComponent))
        conversations[receiver] = sender
    }
}

private fun pmComponent(s: Component, r: Component, text: Component) = Component.text()
    .append(Component.text("[", COLOR_GRAY.textColor))
    .append(s)
    .append(Component.text(" ➤ ", COLOR_PEACH.textColor))
    .append(r)
    .append(Component.text("] ", COLOR_GRAY.textColor))
    .append(text)