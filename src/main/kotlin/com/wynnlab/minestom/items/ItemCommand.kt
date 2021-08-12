package com.wynnlab.minestom.items

import com.wynnlab.minestom.labs.Lab
import com.wynnlab.minestom.playerAtLeast1
import com.wynnlab.minestom.playerAtLeast2
import com.wynnlab.minestom.util.get
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player

object ItemCommand : Command("item") {
    init {
        setCondition { sender, _ -> sender.isPlayer && sender.asPlayer().instance is Lab }

        addSubcommand(Create)
        addSubcommand(ItemBuilderMetaCommand)
        addSubcommand(Get)
    }

    object Create : Command("create") {
        init {
            condition = playerAtLeast2

            val nameArg = ArgumentType.String("item-name")
            val typeArg = ArgumentType.Enum("item-type", ItemType::class.java)

            addSyntax({ sender, ctx ->
                val lab = (sender as Player).instance as Lab
                val id = ItemBuilder.itemBuilderName(sender)
                val builder = ItemBuilder.from(id, ctx[nameArg], ctx[typeArg])
                lab.itemBuilders[id] = builder
                sender.inventory.addItemStack(builder.item())
            }, nameArg, typeArg)
        }
    }

    object Get : Command("get") {
        init {
            condition = playerAtLeast1

            val nameArg = ArgumentType.StringArray("name").setSuggestionCallback { _, ctx, suggestion ->
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
                sender.sendMessage("You get \"${ctx.getRaw(nameArg)}\"")
            }, nameArg)
        }

        private fun getApiItems(name: String): List<String> {
            val json = get("https://api.wynncraft.com/public_api.php?action=itemDB&search=${name.replace(" ", "%20")}")
            val items = json.getAsJsonArray("items")
            return items.mapNotNull { it.asJsonObject.get("name").asString }
        }
    }
}