package com.wynnlab.minestom.base

import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.time.Duration

abstract class BaseSpell(private val duration: Int) : Runnable {
    var t = -1

    private lateinit var task: Task

    @Suppress("EmptyMethod")
    open fun onCast() {}

    @Suppress("EmptyMethod")
    open fun onTick() {}

    @Suppress("EmptyMethod")
    open fun onCancel() {}

    override fun run() {
        ++t

        //println(t)
        onTick()

        if (t >= duration)
            cancel()
    }

    protected fun delay() {
        --t
    }

    protected fun cancel() {
        onCancel()
        task.cancel()
    }

    open fun schedule(delay: Duration = Duration.ZERO, period: Duration = Duration.of(1L, TimeUnit.SERVER_TICK)) {
        //println("Scheduled spell ${this::class.simpleName}")
        task = MinecraftServer.getSchedulerManager().buildTask(this).delay(delay).repeat(period).schedule()
        onCast()
    }
}