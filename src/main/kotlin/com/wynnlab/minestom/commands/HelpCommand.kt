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

object HelpCommand : Command("help", "?") {
    private val help = Book.book(Component.text("Help"), Component.text("WynnLab"),
        Component.text()
            .append(Component.text("play.", COLOR_DARKER_GRAY))
            .append(Component.text("WYNNLAB", COLOR_WYNN, TextDecoration.BOLD))
            .append(Component.text(".tk", COLOR_DARKER_GRAY))
            .append(Component.newline())
            .append(Component.text("help.title"))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.translatable("help.page1",
                Component.text("/lab create", NamedTextColor.BLUE),
                Component.text("/item create", NamedTextColor.BLUE),
                Component.text("/item get", NamedTextColor.BLUE),
                Component.text("/dummy", NamedTextColor.BLUE),
                Component.text("/mob create", NamedTextColor.BLUE),
            ))
            .build(),
    )

    init {
        addSyntax({ sender, _ ->
            sender.openBook(help)
        })

        addSyntax({ sender, ctx ->
            val command = MinecraftServer.getCommandManager().getCommand(ctx.get("command"))
            if (command == null) {
                sender.sendMessage(Component.translatable("command.help.command_not_exist", NamedTextColor.RED))
                return@addSyntax
            }
            sender.sendMessage(Component.text()
                .append(Component.translatable("command.help.field.command", NamedTextColor.WHITE))
                .append(Component.text("/${command.name}", NamedTextColor.AQUA))
                .build())
            if (command is Command) {
                sender.sendMessage(Component.translatable("command.help.field.description", NamedTextColor.GRAY))
                command.description.forEach(sender::sendMessage)
            }
            sender.sendMessage(Component.translatable("command.help.field.usage", NamedTextColor.GRAY))
            command.usage.forEach(sender::sendMessage)
        }, ArgumentType.Word("command"))
    }
}