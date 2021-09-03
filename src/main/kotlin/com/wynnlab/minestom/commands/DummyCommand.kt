package com.wynnlab.minestom.commands

import com.wynnlab.minestom.entities.CustomEntity
import com.wynnlab.minestom.playerAtLeast1
import net.kyori.adventure.text.Component
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.EntityType

object DummyCommand : Command("dummy") {
    init {
        condition = playerAtLeast1

        val healthArg = ArgumentType.Integer("health").min(1).setDefaultValue(100_000)
        val typeArg = ArgumentType.EntityType("type").setDefaultValue(EntityType.ARMOR_STAND)

        addSyntax({ sender, ctx ->
            val player = sender.asPlayer()
            val instance = player.instance ?: return@addSyntax
            val maxHealth = ctx[healthArg].toFloat()

            val dummy = CustomEntity.Default(ctx[typeArg])
            dummy.maxHealth = maxHealth
            dummy.health = maxHealth
            dummy.customName = Component.text("Dummy")
            dummy.isCustomNameVisible = true

            dummy.setInstance(instance, player.position)
        }, healthArg, typeArg)
    }
}