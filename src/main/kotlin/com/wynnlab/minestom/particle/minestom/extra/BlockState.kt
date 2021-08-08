package com.wynnlab.minestom.particle.minestom.extra

import com.wynnlab.minestom.particle.minestom.ParticleType
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.binary.BinaryWriter

@JvmInline
value class BlockState(private val block: Block) : ParticleType.BinaryData {
    override fun accept(t: BinaryWriter) {
        t.writeVarInt(block.blockId.toInt())
    }
}