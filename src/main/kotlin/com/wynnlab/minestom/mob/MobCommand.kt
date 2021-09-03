package com.wynnlab.minestom.mob

import com.wynnlab.minestom.commands.Command
import com.wynnlab.minestom.commands.Subcommand
import com.wynnlab.minestom.entities.CustomEntity
import com.wynnlab.minestom.items.AttackSpeed
import com.wynnlab.minestom.items.Damage
import com.wynnlab.minestom.items.Defense
import com.wynnlab.minestom.labs.Lab
import com.wynnlab.minestom.playerAtLeast2
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.sound.SoundEvent

object MobCommand : Command(arrayOf(
    Component.translatable("command.mob.description", NamedTextColor.GREEN)
), "mob") {
    init {
        setCondition { sender, _ -> sender.isPlayer && sender.asPlayer().instance is Lab }

        addSubcommand(Create)
        addSubcommand(Meta)
        addSubcommand(Name)
        addSubcommand(Type)
        addSubcommand(Sound)
        addSubcommand(Spawn)
        addSubcommand(Build)
    }

    object Create : Subcommand("create") {
        init {
            condition = playerAtLeast2

            val nameArg = ArgumentType.String("name")
            val typeArg = ArgumentType.EntityType("type")
            val levelArg = ArgumentType.Integer("level").setDefaultValue(1)

            addSyntax({ sender, ctx ->
                val lab = (sender as Player).instance as Lab
                val id = MobBuilder.mobBuilderName(sender)
                val builder = MobBuilder(id, ctx[nameArg], ctx[typeArg], ctx[levelArg])
                lab.mobBuilders[id] = builder
                sender.inventory.addItemStack(builder.item())
            }, nameArg, typeArg, levelArg)
        }
    }

    object Meta : Subcommand("meta") {
        init {
            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.level = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("level"), ArgumentType.Integer("value"))

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.attackSpeed = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("max-health"), ArgumentType.Enum("value", AttackSpeed::class.java))

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.maxHealth = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("max-health"), ArgumentType.Integer("value").min(1))

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.baseDefense = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("base-defense"), ArgumentType.Float("value"))

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                val field = ctx.get<String>("element")
                val value = ctx.get<Int>("value")
                Defense::class.java.getField(field)[builder.elementalDefense] = value
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("defense"), ArgumentType.Word("element").from(
                "earth", "thunder", "water", "fire", "air"
            ), ArgumentType.Integer("value"))

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                val field = ctx.get<String>("element")
                val value = ctx.get<net.minestom.server.utils.math.IntRange>("value")
                Damage::class.java.getField(field).set(builder.damage, value.minimum..value.maximum)
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("damage"), ArgumentType.Word("element").from(
                "neutral", "earth", "thunder", "water", "fire", "air"
            ), ArgumentType.IntRange("value"))

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.glowing = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("glowing"), ArgumentType.Boolean("value"))

            /*addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.invisible = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("invisible"), ArgumentType.Boolean("value"))*/

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.burning = ctx.get("value")
                sender.itemInMainHand = builder.item()
            }, ArgumentType.Literal("burning"), ArgumentType.Boolean("value"))
        }
    }

    object Name : Subcommand("name") {
        init {
            val nameArg = ArgumentType.String("name")

            addSyntax({ sender, ctx ->
                println(sender.asPlayer().itemInMainHand.getTag(MobBuilder.nameTag))
                println((sender.asPlayer().instance as Lab).mobBuilders)
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.name = ctx[nameArg]
                sender.itemInMainHand = builder.item()
            }, nameArg)
        }
    }

    object Type : Subcommand("type") {
        init {
            val typeArg = ArgumentType.EntityType("type")

            addSyntax({ sender, ctx ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                builder.type = ctx[typeArg]
                sender.itemInMainHand = builder.item()
            }, typeArg)
        }
    }

    object Sound : Subcommand("sound") {
        init {
            addSubcommand(Set)
            addSubcommand(Remove)
        }

        object Set : Subcommand("set") {
            init {
                val soundArg = ArgumentType.Word("sound").from("Hurt", "Death", "Ambient")
                val typeArg = ArgumentType.String("type").setSuggestionCallback { _, _, suggestion ->
                    val input = suggestion.input.let { if (it.startsWith('"')) it.substring(1) else it }
                        .let { if (it.startsWith("minecraft:")) it.substring(10) else it }
                    SoundEvent.values().forEach {
                        val namespace = it.namespace()
                        if (namespace.path.startsWith(input, ignoreCase = true)) suggestion.addEntry(SuggestionEntry("\"$namespace\""))
                    }
                }
                val pitchArg = ArgumentType.Float("pitch").between(.5f, 2f).setDefaultValue(1f)

                addSyntax({ sender, ctx ->
                    val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                    val sound = Sound(SoundEvent.fromNamespaceId(ctx[typeArg]) ?: return@addSyntax, ctx[pitchArg])
                    when (ctx[soundArg]) {
                        "Hurt" -> builder.hurtSound = sound
                        "Death" -> builder.deathSound = sound
                        "Ambient" -> builder.ambientSound = sound
                    }
                    sender.itemInMainHand = builder.item()
                }, soundArg, typeArg, pitchArg)
            }
        }

        object Remove : Subcommand("remove") {
            init {
                val soundArg = ArgumentType.Word("sound").from("Hurt", "Death", "Ambient")

                addSyntax({ sender, ctx ->
                    val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                    when (ctx[soundArg]) {
                        "Hurt" -> builder.hurtSound = null
                        "Death" -> builder.deathSound = null
                        "Ambient" -> builder.ambientSound = null
                    }
                    sender.itemInMainHand = builder.item()
                }, soundArg)
            }
        }
    }

    object Spawn : Subcommand("spawn") {
        init {
            addSyntax({ sender, _ ->
                val mob = getCustomMob((sender as Player).itemInMainHand)
                if (mob == null) {
                    sender.sendMessage("§cYou can't spawn this")
                    return@addSyntax
                }
                spawn(sender.instance ?: return@addSyntax, sender.position, mob)
            })
        }
    }

    object Build : Subcommand("build") {
        init {
            addSyntax({ sender, _ ->
                val builder = getMobBuilder(sender as Player) ?: return@addSyntax
                sender.inventory.addItemStack(builder.item(true))
            })
        }
    }

    fun getMobBuilder(sender: Player) =
        sender.itemInMainHand.getTag(MobBuilder.nameTag)?.let {
            (sender.instance as Lab).mobBuilders[it]
        }

    fun spawn(instance: Instance, pos: Pos, mob: CustomEntity) {
        MobBuilder.setCustomEntityBelowNameTag(mob)
        mob.setInstance(instance, pos)
    }
}