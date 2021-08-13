@file:Suppress("PackageDirectoryMismatch")

package net.minestom.server.inventory

import net.minestom.server.item.ItemStack

val AbstractInventory.itemStacksRaw: Array<ItemStack> get() = this.itemStacks