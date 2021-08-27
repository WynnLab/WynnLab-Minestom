package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleTypes
import net.minestom.server.item.ItemStack
import net.minestom.server.utils.binary.BinaryWriter

@JvmInline
value class Item(val itemStack: ItemStack) : ParticleTypes.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeItemStack(itemStack)
    }
}