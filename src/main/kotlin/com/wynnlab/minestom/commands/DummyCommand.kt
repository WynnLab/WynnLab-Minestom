package com.wynnlab.minestom.commands

import com.wynnlab.minestom.core.damage.maxHealthTag
import net.kyori.adventure.text.Component
import net.minestom.server.attribute.Attribute
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity

object DummyCommand : Command("dummy") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        addSyntax({ sender, _ ->
            val player = sender.asPlayer()
            val instance = player.instance ?: return@addSyntax

            val dummy = LivingEntity(EntityType.VINDICATOR)
            dummy.getAttribute(Attribute.MAX_HEALTH).baseValue = 20f
            dummy.health = 20f
            dummy.customName = Component.text("Dummy")
            dummy.isCustomNameVisible = true

            dummy.setTag(maxHealthTag, 100)

            dummy.setInstance(instance, player.position)
        })
    }
}