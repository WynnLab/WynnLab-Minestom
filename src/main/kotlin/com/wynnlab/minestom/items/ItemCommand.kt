package com.wynnlab.minestom.items

import com.wynnlab.minestom.commands.Command
import com.wynnlab.minestom.commands.Subcommand
import com.wynnlab.minestom.labs.Lab
import com.wynnlab.minestom.playerAtLeast1
import com.wynnlab.minestom.playerAtLeast2
import com.wynnlab.minestom.util.get
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player

object ItemCommand : Command(arrayOf(
    Component.translatable("command.item.description"),
    Component.text("/get: ", NamedTextColor.AQUA).append(Component.translatable("command.item.get.description", NamedTextColor.GRAY)),
    Component.text("/create: ", NamedTextColor.AQUA).append(Component.translatable("command.item.create.description", NamedTextColor.GRAY)),
    Component.text("/id: ", NamedTextColor.AQUA).append(Component.translatable("command.item.id.description", NamedTextColor.GRAY)),
    Component.text("/meta: ", NamedTextColor.AQUA).append(Component.translatable("command.item.meta.description", NamedTextColor.GRAY)),
    Component.text("/name: ", NamedTextColor.AQUA).append(Component.translatable("command.item.name.description", NamedTextColor.GRAY)),
    Component.text("/lore: ", NamedTextColor.AQUA).append(Component.translatable("command.item.lore.description", NamedTextColor.GRAY)),
    Component.text("/design: ", NamedTextColor.AQUA).append(Component.translatable("command.item.design.description", NamedTextColor.GRAY)),
    Component.text("/build: ", NamedTextColor.AQUA).append(Component.translatable("command.item.build.description", NamedTextColor.GRAY)),
), "item") {
    init {
        setCondition { sender, _ -> sender.isPlayer && sender.asPlayer().instance is Lab }

        addSubcommand(Create)
        addSubcommand(ItemMetaCommand)
        addSubcommand(Identification)
        addSubcommand(Name)
        addSubcommand(Lore)
        addSubcommand(Design)
        addSubcommand(Build)
        addSubcommand(Get)
    }

    object Create : Subcommand("create") {
        init {
            condition = playerAtLeast2

            val nameArg = ArgumentType.String("item-name")
            val typeArg = ArgumentType.Enum("item-type", ItemType::class.java)

            addSyntax({ sender, ctx ->
                val lab = (sender as Player).instance as Lab
                val id = ItemBuilder.itemBuilderName(sender)
                val builder = ItemBuilder.from(id, ctx[nameArg], ctx[typeArg])
                lab.itemBuilders[id] = builder
                sender.inventory.addItemStack(builder.itemFor(sender))
            }, nameArg, typeArg)
        }
    }

    object Identification : Subcommand("identification", "id") {
        init {
            val idArg = ArgumentType.Enum("identification", com.wynnlab.minestom.items.Identification::class.java)
            val valueArg = ArgumentType.Integer("value")

            addSyntax({ sender, ctx ->
                val builder = getItemBuilder(sender as Player) ?: return@addSyntax
                builder.setId(ctx[idArg], ctx[valueArg].toShort())
                sender.itemInMainHand = builder.itemFor(sender)
            }, idArg, valueArg)
        }
    }

    object Name : Subcommand("name") {
        init {
            val nameArg = ArgumentType.String("name")

            addSyntax({ sender, ctx ->
                val builder = getItemBuilder(sender as Player) ?: return@addSyntax
                builder.name = ctx[nameArg]
                sender.itemInMainHand = builder.itemFor(sender)
            }, nameArg)
        }
    }

    object Lore : Subcommand("lore") {
        init {
            val loreArg = ArgumentType.StringArray("lore")

            addSyntax({ sender, ctx ->
                val builder = getItemBuilder(sender as Player) ?: return@addSyntax
                builder.setCustomLore(ctx[loreArg])
                sender.itemInMainHand = builder.itemFor(sender)
            }, loreArg)
        }
    }

    object Design : Subcommand("design") {
        init {
            val designArg = ArgumentType.Word("design").setSuggestionCallback { sender, _, suggestion ->
                getItemBuilder(sender as Player)?.type?.designs?.keys?.forEach { k ->
                    if (k.startsWith(suggestion.input, true)) suggestion.addEntry(SuggestionEntry(k))
                }
            }

            addSyntax({ sender, ctx ->
                val builder = getItemBuilder(sender as Player) ?: return@addSyntax
                builder.setDesign(ctx[designArg])
                sender.itemInMainHand = builder.itemFor(sender)
            }, designArg)
        }
    }

    object Build : Subcommand("build") {
        init {
            addSyntax({ sender, _ ->
                val builder = getItemBuilder(sender as Player) ?: return@addSyntax
                sender.inventory.addItemStack(builder.itemFor(sender))
            })
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    object Get : Subcommand("get") {
        init {
            condition = playerAtLeast1

            val nameArg = ArgumentType.StringArray("name").setSuggestionCallback { _, ctx, suggestion ->
                val itemName = ctx.input.substring(suggestion.start - 1)
                if (itemName.isEmpty()) return@setSuggestionCallback

                if (itemName.length <= 3) {
                    suggestion.addEntry(SuggestionEntry(itemName))
                    return@setSuggestionCallback
                }

                runBlocking {
                    val results = getApiItemsAsync(itemName).await()
                    for (r in results) {
                        suggestion.addEntry(SuggestionEntry(r))
                    }
                }
            }

            addSyntax({ sender, ctx ->
                try {
                    val name = ctx.getRaw(nameArg)
                    sender.sendMessage(Component.translatable("command.item.get.searching", Component.text(name)))
                    GlobalScope.launch {
                        val r = get("https://api.wynncraft.com/public_api.php?action=itemDB&search=${name.replace(" ", "%20")}").await()
                        val item = (r["items"] ?: return@launch).jsonArray.let {
                                a -> a.find { it.jsonObject["name"]?.jsonPrimitive?.contentOrNull?.equals(name, true) == true }
                            ?: try { a.first() } catch (_: NoSuchElementException) {
                                sender.sendMessage(Component.text("command.item.get.not_found", NamedTextColor.RED))
                                return@launch
                            }
                        }
                        val builder = itemBuilderFrom(item)
                        (sender as Player).inventory.addItemStack(builder.itemFor(sender))
                    }
                } catch (e: Exception) {
                    sender.sendMessage(Component.text("command.item.get.other_error", NamedTextColor.RED))
                    e.printStackTrace()
                }
            }, nameArg)
        }

        private suspend fun getApiItemsAsync(name: String): Deferred<List<String>> = coroutineScope {
            async {
                try {
                    val r = get("https://api.wynncraft.com/public_api.php?action=itemDB&search=${name.replace(" ", "%20")}").await()
                    val items = r["items"]?.jsonArray ?: JsonArray(emptyList())
                    items.mapNotNull { it.jsonObject["name"]?.jsonPrimitive?.contentOrNull }
                } catch (_: Exception) {
                    emptyList()
                }
            }
        }
    }

    fun getItemBuilder(sender: Player) =
        sender.itemInMainHand.getTag(ItemBuilder.nameTag)?.let {
            (sender.instance as Lab).itemBuilders[it]
        }
}