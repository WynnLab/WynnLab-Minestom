package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleType
import net.minestom.server.item.ItemStack
import net.minestom.server.utils.binary.BinaryWriter

@JvmInline
value class Item(val itemStack: ItemStack) : ParticleType.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeItemStack(itemStack)
    }
}