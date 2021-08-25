@file:Suppress("PackageDirectoryMismatch")

package net.minestom.server.item

import net.kyori.adventure.text.Component

fun ItemStack.builder() = ItemStackBuilder(material, meta.builder()).amount(amount).stackingRule(stackingRule)

val ItemStackBuilder.lore: List<Component> get() = metaBuilder.lore