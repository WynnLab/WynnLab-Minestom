@file:Suppress("PackageDirectoryMismatch")

package net.minestom.server.item

fun ItemStack.builder() = ItemStackBuilder(material, meta.builder()).amount(amount).stackingRule(stackingRule)