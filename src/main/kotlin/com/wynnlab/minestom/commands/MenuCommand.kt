package com.wynnlab.minestom.commands

import com.wynnlab.minestom.gui.MenuGui

object MenuCommand : Command("Open the compass menu", "menu") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSyntax({ sender, _ ->
            MenuGui.show(sender.asPlayer())
        })
    }
}