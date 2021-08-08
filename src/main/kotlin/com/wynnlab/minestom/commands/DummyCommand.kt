package com.wynnlab.minestom.commands

import com.wynnlab.minestom.core.damage.maxHealthTag
import com.wynnlab.minestom.entities.LivingEntityWithBelowNameTag
import net.kyori.adventure.text.Component
import net.minestom.server.attribute.Attribute
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.EntityType

object DummyCommand : Command("dummy") {
    init {
        setCondition { sender, _ -> sender.isPlayer }

        val healthArg = ArgumentType.Integer("health").min(1).setDefaultValue(100_000)

        addSyntax({ sender, ctx ->
            val player = sender.asPlayer()
            val instance = player.instance ?: return@addSyntax

            val dummy = LivingEntityWithBelowNameTag(EntityType.VINDICATOR)
            dummy.getAttribute(Attribute.MAX_HEALTH).baseValue = 20f
            dummy.health = 20f
            dummy.customName = Component.text("Dummy")
            dummy.isCustomNameVisible = true

            dummy.setTag(maxHealthTag, ctx[healthArg])

            dummy.setInstance(instance, player.position)
        }, healthArg)
    }
}