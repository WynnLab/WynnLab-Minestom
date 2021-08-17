package com.wynnlab.minestom.commands

import com.wynnlab.minestom.COLOR_DARKER_GRAY
import com.wynnlab.minestom.COLOR_WYNN
import com.wynnlab.minestom.textColor
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.arguments.ArgumentType

object HelpCommand : Command("Show the help pages","help", "?") {
    private val help = Book.book(Component.text("Help"), Component.text("WynnLab"),
        Component.text()
            .append(Component.text("play.", COLOR_DARKER_GRAY.textColor))
            .append(Component.text("WYNNLAB", COLOR_WYNN.textColor, TextDecoration.BOLD))
            .append(Component.text(".tk", COLOR_DARKER_GRAY.textColor))
            .append(Component.newline())
            .append(Component.text("The official Help book"))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Create your personal lab with "))
            .append(Component.text("/lab create", NamedTextColor.BLUE))
            .append(Component.text(", create items using "))
            .append(Component.text("/item create", NamedTextColor.BLUE))
            .append(Component.text(" or get them from the official Wynncraft api using "))
            .append(Component.text("/item get", NamedTextColor.BLUE))
            .append(Component.text(". You can spawn a dummy to test your skills with "))
            .append(Component.text("/dummy", NamedTextColor.BLUE))
            .append(Component.text("."))
            .build(),
    )

    init {
        addSyntax({ sender, _ ->
            sender.openBook(help)
        })

        addSyntax({ sender, ctx ->
            val command = MinecraftServer.getCommandManager().getCommand(ctx.get<String>("command"))
            if (command == null) {
                sender.sendMessage("§cThis command doesn't exist")
                return@addSyntax
            }
            sender.sendMessage("§fCommand: §b/${command.name}")
            if (command is Command) {
                sender.sendMessage("§7Description:")
                command.description.forEach(sender::sendMessage)
            }
            sender.sendMessage("§7Usage:")
            command.usage.forEach(sender::sendMessage)
        }, ArgumentType.Word("command"))
    }
}