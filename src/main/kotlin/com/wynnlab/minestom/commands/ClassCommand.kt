package com.wynnlab.minestom.commands

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry

object ClassCommand : Command("class") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        setDefaultExecutor { sender, _ ->
            sender.sendMessage("Choose a class")
        }

        val nameArg = ArgumentType.String("name").setSuggestionCallback { _, _, suggestion ->
            suggestion.entries.addAll(nameSuggestions)
        }

        addSyntax({ sender, ctx ->
            sender.sendMessage("Your class is now ${ctx[nameArg]}")
        }, nameArg)
    }

    private val nameSuggestions = listOf(
        SuggestionEntry("archer"),
        SuggestionEntry("assassin"),
        SuggestionEntry("mage"),
        SuggestionEntry("shaman"),
        SuggestionEntry("warrior"),
    )
}