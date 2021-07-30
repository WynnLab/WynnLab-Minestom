package com.wynnlab.minestom.commands

import com.wynnlab.minestom.gui.MenuGui
import net.minestom.server.command.builder.Command

object MenuCommand : Command("menu") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSyntax({ sender, _ ->
            MenuGui().show(sender.asPlayer())
        })
    }
}