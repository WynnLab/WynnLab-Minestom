package com.wynnlab.minestom.base

import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.BooleanTag
import net.minestom.server.tag.Tag

abstract class BaseClass(
    val id: String,
    val item: ItemStack,
    val metaStats: MetaStats,
) {
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