package com.wynnlab.minestom.items

import com.wynnlab.minestom.commands.Subcommand
import com.wynnlab.minestom.items.ItemCommand.getItemBuilder
import com.wynnlab.minestom.playerAtLeast2
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

object ItemMetaCommand : Subcommand("meta") {
    init {
        condition = playerAtLeast2

        addSyntax({ sender, ctx ->
            val itemBuilder = getItemBuilder(sender as Player) ?: return@addSyntax
            val rarity = ctx.get<Rarity>("value")
            itemBuilder.rarity = rarity
            sender.itemInMainHand = itemBuilder.item()
        }, ArgumentType.Literal("rarity"), ArgumentType.Enum("value", Rarity::class.java))

        addSyntax(
            { sender, ctx ->
                val itemBuilder = getItemBuilder(sender as Player) ?: return@addSyntax
                val field = ctx.get<String>("skill")
                val value = ctx.get<Int>("value")
                SkillRequirements::class.java.getField(field).set(itemBuilder.skillRequirements, value)
                sender.itemInMainHand = itemBuilder.item()
            }, ArgumentType.Literal("skill-req"), ArgumentType.Word("skill").from(
                "strength", "dexterity", "intelligence", "defense", "agility"
            ), ArgumentType.Integer("value")
        )

        addSyntax({ sender, ctx ->
            val itemBuilder = getItemBuilder(sender as Player) ?: return@addSyntax
            if (itemBuilder !is ItemBuilder.Weapon) {
                warnWrongType(sender)
                return@addSyntax
            }
            val speed = ctx.get<AttackSpeed>("value")
            itemBuilder.attackSpeed = speed
            sender.itemInMainHand = itemBuilder.item()
        }, ArgumentType.Literal("attack-speed"), ArgumentType.Enum("value", AttackSpeed::class.java))

        addSyntax(
            { sender, ctx ->
                val itemBuilder = getItemBuilder(sender as Player) ?: return@addSyntax
                if (itemBuilder !is ItemBuilder.Weapon) {
                    warnWrongType(sender)
                    return@addSyntax
                }
                val field = ctx.get<String>("element")
                val value = ctx.get<net.minestom.server.utils.math.IntRange>("value")
                Damage::class.java.getField(field).set(itemBuilder.damage, value.minimum..value.maximum)
                sender.itemInMainHand = itemBuilder.item()
            }, ArgumentType.Literal("damage"), ArgumentType.Word("element").from(
                "neutral", "earth", "thunder", "water", "fire", "air"
            ), ArgumentType.IntRange("value")
        )

        addSyntax({ sender, ctx ->
            val itemBuilder = getItemBuilder(sender as Player) ?: return@addSyntax
            if (itemBuilder !is ItemBuilder.Defense) {
                warnWrongType(sender)
                return@addSyntax
            }
            val health = ctx.get<Int>("value")
            itemBuilder.health = health
            sender.itemInMainHand = itemBuilder.item()
        }, ArgumentType.Literal("health"), ArgumentType.Integer("value"))

        addSyntax(
            { sender, ctx ->
                val itemBuilder = getItemBuilder(sender as Player) ?: return@addSyntax
                if (itemBuilder !is ItemBuilder.Defense) {
                    warnWrongType(sender)
                    return@addSyntax
                }
                val field = ctx.get<String>("element")
                val value = ctx.get<Int>("value")
                Defense::class.java.getField(field).set(itemBuilder.defense, value)
                sender.itemInMainHand = itemBuilder.item()
            }, ArgumentType.Literal("defense"), ArgumentType.Word("element").from(
                "earth", "thunder", "water", "fire", "air"
            ), ArgumentType.Integer("value")
        )
    }

    private fun warnWrongType(sender: Player) = sender.sendMessage("§cThis item does not have this property")
}