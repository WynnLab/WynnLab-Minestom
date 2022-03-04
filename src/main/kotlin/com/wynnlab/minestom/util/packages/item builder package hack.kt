@file:Suppress("PackageDirectoryMismatch")

package net.minestom.server.item

import net.kyori.adventure.text.Component

fun ItemStack.builder() = ItemStackBuilder(material, meta.builder()).amount(amount).stackingRule(stackingRule)

val ItemStackBuilder.lore: MutableList<Component> //get() = metaBuilder.lore
    get() {
        var l: MutableList<Component>? = null
        meta<ItemMetaBuilder> { l = it.lore; it }
        return l!!
    }