package com.wynnlab.minestom.commands

import com.wynnlab.minestom.gui.ClassGui
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry

object ClassCommand : Command("class") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        val nameArg = ArgumentType.Word("name")
            .from("archer", "assassin", "mage", "shaman", "warrior")

        addSyntax({ sender, _ ->
            ClassGui().show(sender.asPlayer())
        })

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