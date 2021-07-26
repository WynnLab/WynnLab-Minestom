package com.wynnlab.minestom.commands

import com.wynnlab.minestom.io.getApiResultsJson
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry

object ItemCommand : Command("item") {
    init {
        val nameArg = ArgumentType.StringArray("name").setSuggestionCallback { sender, ctx, suggestion ->
            val itemName = ctx.input.substring(suggestion.start - 1)
            if (itemName.isEmpty()) return@setSuggestionCallback

            if (itemName.length <= 3) {
                suggestion.addEntry(SuggestionEntry(itemName))
                return@setSuggestionCallback
            }

            val results = getApiItems(itemName)
            for (r in results) {
                suggestion.addEntry(SuggestionEntry(r))
            }
        }

        addSyntax({ sender, ctx ->
            sender.sendMessage("You get \"${ctx[nameArg].joinToString(" ")}\"")
        }, nameArg)
    }

    private fun getApiItems(name: String): List<String> {
        val json = getApiResultsJson("https://api.wynncraft.com/public_api.php?action=itemDB&search=${name.replace(" ", "%20")}")
        val items = json.getAsJsonArray("items")
        return items.mapNotNull { it.asJsonObject.get("name").asString }
    }
}