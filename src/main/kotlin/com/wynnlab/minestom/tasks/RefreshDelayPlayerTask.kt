package com.wynnlab.minestom.tasks

import net.minestom.server.MinecraftServer
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagWritable
import java.time.temporal.TemporalUnit

abstract class RefreshDelayTask<DataContainer>(
    private val data: DataContainer,
    id: String
) : Runnable where DataContainer : TagReadable, DataContainer : TagWritable {
    private val tag = Tag.Integer("RDT-$id").defaultValue(0)

    final override fun run() {
        val delay = data.getTag(tag)!!
        if (delay > 0)
            data.setTag(tag, delay - 1)
        if (delay <= 1) {
            task()
            data.removeTag(tag)
        }
    }

    abstract fun task()

    fun schedule(delay: Long, unit: TemporalUnit) {
        MinecraftServer.getSchedulerManager().buildTask(this).delay(delay, unit).schedule()
        data.setTag(tag, data.getTag(tag)!! + 1)
    }
}

inline fun <DataContainer> RefreshDelayTask(
    data: DataContainer,
    id: String,
    crossinline task: () -> Unit
) where DataContainer : TagReadable, DataContainer : TagWritable =
    object : RefreshDelayTask<DataContainer>(data, id) {
        override fun task() {
            task()
        }
    }