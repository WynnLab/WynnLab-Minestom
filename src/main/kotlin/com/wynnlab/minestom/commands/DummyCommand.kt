package com.wynnlab.minestom.commands

import com.wynnlab.minestom.entities.CustomEntity
import net.kyori.adventure.text.Component
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
            val maxHealth = ctx[healthArg].toFloat()

            val dummy = CustomEntity.Default(EntityType.VINDICATOR)
            dummy.maxHealth = maxHealth
            dummy.health = maxHealth
            dummy.customName = Component.text("Dummy")
            dummy.isCustomNameVisible = true



            dummy.setInstance(instance, player.position)
        }, healthArg)
    }
}