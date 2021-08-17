package com.wynnlab.minestom.commands

import net.minestom.server.command.builder.arguments.ArgumentType

object CastCommand : Command("Cast a spell", "cast", sendConsole = false) {
    init {
        val spellArg = ArgumentType.Integer("spell id").between(0, 4)
        addSyntax({ sender, ctx ->
            sender.sendMessage("Cast #${ctx[spellArg]}")
        }, spellArg)
    }
}