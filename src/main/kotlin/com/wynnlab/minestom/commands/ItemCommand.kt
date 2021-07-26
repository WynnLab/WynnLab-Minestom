package com.wynnlab.minestom.commands

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry

object ItemCommand : Command("item") {
    init {
        val nameArg = ArgumentType.StringArray("name").setSuggestionCallback { sender, ctx, suggestion ->
            suggestion.addEntry(SuggestionEntry("hello world"))
        }

        addSyntax({ sender, ctx ->
            sender.sendMessage("You get \"${ctx[nameArg].joinToString(" ")}\"")
        }, nameArg)
    }
}