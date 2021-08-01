package com.wynnlab.minestom.util

import net.minestom.server.inventory.AbstractInventory
import net.minestom.server.item.ItemStack

fun AbstractInventory.isEmpty(slot: Int) = getItemStack(slot).isAir

fun AbstractInventory.setIfEmpty(slot: Int, itemStack: ItemStack) { if (isEmpty(slot)) setItemStack(slot, itemStack) }