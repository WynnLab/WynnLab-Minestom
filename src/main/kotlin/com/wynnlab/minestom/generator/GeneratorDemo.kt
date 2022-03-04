package com.wynnlab.minestom.generator

import net.minestom.server.instance.Chunk
import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block

class GeneratorDemo : ChunkGenerator {
    override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
        for (x in 0 until Chunk.CHUNK_SIZE_X)
            for (z in 0 until Chunk.CHUNK_SIZE_Z)
                for (y in 0 until 40)
                    batch.setBlock(x, y, z, if (y < 35) Block.STONE else if (y < 39) Block.DIRT else Block.GRASS_BLOCK)
    }

    /*override fun fillBiomes(biomes: Array<Biome>, chunkX: Int, chunkZ: Int) {
        biomes.fill(Biome.PLAINS)
    }*/

    override fun getPopulators(): MutableList<ChunkPopulator>? = null
}