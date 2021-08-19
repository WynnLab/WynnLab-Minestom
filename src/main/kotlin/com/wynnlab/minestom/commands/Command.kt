package com.wynnlab.minestom.commands

import net.kyori.adventure.text.Component
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.CommandSyntax
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.ArgumentLiteral

abstract class Command private constructor(
    val description: Array<out Component>,
    name: String,
    aliases: Array<out String>,
    sendConsole: Boolean
) : Subcommand(aliases, name) {
    init {
        if (!sendConsole) {
            consoleIgnoreCommands.add(this.name)
            this.aliases?.let {
                @Suppress("unchecked_cast") consoleIgnoreCommands.addAll(it as Array<out String>)
            }
        }
    }

    constructor(description: String, name: String, vararg aliases: String, sendConsole: Boolean = true) :
            this(arrayOf(Component.text(description)), name, aliases, sendConsole)

    constructor(description: Array<out Component>, name: String, vararg aliases: String = arrayOf()) :
            this(description, name, aliases, true)
}

val consoleIgnoreCommands = mutableListOf<String>()

abstract class Subcommand : Command {
    init {
        setDefaultExecutor { sender, _ ->
            sender.sendMessage("§cInvalid Syntax! §bUsage:")
            usage.forEach(sender::sendMessage)
        }
    }

    final override fun setDefaultExecutor(executor: CommandExecutor?) {
        super.setDefaultExecutor(executor)
    }

    protected constructor(aliases: Array<out String>, name: String) : super(name, *aliases)

    constructor(name: String, vararg aliases: String) : super(name, *aliases)

    constructor(name: String) : super(name)
}

typealias RawCommand = Command

val RawCommand.usage get() = mutableListOf<String>().apply {
    syntaxes.usage()?.let { add("/$name$it") }
    subcommands.mapNotNullTo(this) {
        it.syntaxes.usage()?.let { u -> "/$name ${it.name}$u" }
    }
}

private fun Collection<CommandSyntax>.usage() = when (size) {
    0 -> null
    1 -> " ${iterator().next().arguments.usage()}"
    else -> joinToString(" | ", " (", ")", 3, "...") { it.arguments.usage() }
}

private fun Array<Argument<*>>.usage() = this.joinToString(" ") { it.usage() }

private fun Argument<*>.usage(ignoreOptional: Boolean = false): String =
    if (isOptional && !ignoreOptional) "[${usage(true)}]"
    else if (this is ArgumentLiteral) id
    else "<$id>"