package com.wynnlab.minestom.base

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.BooleanTag
import net.minestom.server.tag.Tag

abstract class BaseClass(
    val id: String,
    item: ItemStack,
    val metaStats: MetaStats,
) {
    val item: ItemStack = item.withDisplayName(Component.translatable("class.$id.name"))

    open val invertedControls get() = false
    abstract val spells: List<(Player) -> BasePlayerSpell>

    data class MetaStats(
        val damage: Int,
        val defense: Int,
        val range: Int,
        val spells: Int
    )
}

val playerClassTag = Tag.String("class")
val playerCloneClassTag = BooleanTag("clone-class")