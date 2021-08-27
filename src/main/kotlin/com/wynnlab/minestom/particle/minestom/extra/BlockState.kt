package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleTypes
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.binary.BinaryWriter

@JvmInline
value class BlockState(private val block: Block) : ParticleTypes.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeVarInt(block.id())
    }
}