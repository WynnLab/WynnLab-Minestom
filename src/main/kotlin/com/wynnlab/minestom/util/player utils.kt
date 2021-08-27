package com.wynnlab.minestom.util

import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import kotlin.math.cos
import kotlin.math.sin

val Entity.eyePos get() = position.add(.0, eyeHeight, .0)
val Entity.dirVec: Vec get() {
    val yaw = position.yaw().toDouble()
    val pitch = position.pitch().toDouble()
    val cp = cos(pitch)
    return Vec(cos(yaw) * cp, sin(pitch), sin(yaw) * cp)
}